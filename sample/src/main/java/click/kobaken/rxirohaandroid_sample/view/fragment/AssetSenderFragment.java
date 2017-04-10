/*
Copyright(c) 2016 kobaken0029 All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package click.kobaken.rxirohaandroid_sample.view.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import click.kobaken.rxirohaandroid_sample.R;
import click.kobaken.rxirohaandroid_sample.databinding.FragmentAssetSenderBinding;
import click.kobaken.rxirohaandroid_sample.exception.ErrorMessageFactory;
import click.kobaken.rxirohaandroid_sample.exception.IllegalQRCodeException;
import click.kobaken.rxirohaandroid_sample.model.TransferQRParameter;
import click.kobaken.rxirohaandroid_sample.presenter.AssetSenderPresenter;
import click.kobaken.rxirohaandroid_sample.view.AssetSenderView;
import click.kobaken.rxirohaandroid_sample.view.activity.MainActivity;
import click.kobaken.rxirohaandroid_sample.view.activity.QRScannerActivity;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class AssetSenderFragment extends Fragment
        implements AssetSenderView, MainActivity.MainActivityListener {
    public static final String TAG = AssetSenderFragment.class.getSimpleName();

    private AssetSenderPresenter assetSenderPresenter = new AssetSenderPresenter();

    private FragmentAssetSenderBinding binding;
    private SweetAlertDialog sweetAlertDialog;

    public static AssetSenderFragment newInstance() {
        AssetSenderFragment fragment = new AssetSenderFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assetSenderPresenter.setView(this);
        assetSenderPresenter.onCreate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_asset_sender, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = DataBindingUtil.bind(view);
        binding.qrButton.setOnClickListener(assetSenderPresenter.onQRShowClicked());
        binding.submitButton.setOnClickListener(assetSenderPresenter.onSubmitClicked());
        binding.amount.addTextChangedListener(assetSenderPresenter.textWatcher());
    }

    @Override
    public void onStart() {
        super.onStart();
        assetSenderPresenter.onStart();
        if (binding.receiver.getText().length() != 0) {
            Log.d(TAG, "onStart: " + binding.receiver.getText().toString());
            afterQRReadViewState(
                    binding.receiver.getText().toString(),
                    binding.amount.getText().toString()
            );
        } else {
            beforeQRReadViewState();
        }
    }

    @Override
    public void onStop() {
        assetSenderPresenter.onStop();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        assetSenderPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                Log.e(TAG, "onActivityResult: Canceled!");
                return;
            }

            TransferQRParameter params;
            try {
                params = new Gson().fromJson(result.getContents(), TransferQRParameter.class);
            } catch (Exception e) {
                Log.e(TAG, "onActivityResult: json could not parse to object!");
                showError(ErrorMessageFactory.create(getContext(), new IllegalQRCodeException()));
                return;
            }

            final String value = String.valueOf(params.amount).equals("0")
                    ? ""
                    : String.valueOf(params.amount);
            Log.d(TAG, "onActivityResult: " + value);
            afterQRReadViewState(params.account, value);
        }
    }

    @Override
    public void showError(String error) {
        sweetAlertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
        sweetAlertDialog.setTitleText(getString(R.string.error))
                .setContentText(error)
                .show();
    }

    @Override
    public void showWarning(String warning) {
        sweetAlertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog.setTitleText(getString(R.string.warning))
                .setContentText(warning)
                .show();
    }

    @Override
    public void showSuccess(String title, String message, View.OnClickListener onClickListener) {
        sweetAlertDialog.setTitleText(title)
                .setContentText(message)
                .setConfirmClickListener(dialog -> onClickListener.onClick(null))
                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
        sweetAlertDialog.show();
    }

    @Override
    public void hideSuccess() {
        sweetAlertDialog.dismiss();
    }

    @Override
    public String getAmount() {
        return binding.amount.getText().toString();
    }

    @Override
    public void setAmount(String amount) {
        binding.amount.setText(amount);
    }

    @Override
    public String getReceiver() {
        Log.d(TAG, "getReceiver: " + binding.receiver.getText().toString());
        return binding.receiver.getText().toString();
    }

    @Override
    public void showQRReader() {
        IntentIntegrator.forSupportFragment(this)
                .setBeepEnabled(false)
                .setOrientationLocked(true)
                .setBarcodeImageEnabled(true)
                .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
                .setCaptureActivity(QRScannerActivity.class)
                .initiateScan();
    }

    @Override
    public void beforeQRReadViewState() {
        binding.receiver.setText("");
        binding.amount.setText("");
    }

    @Override
    public void afterQRReadViewState(String receiver, String value) {
        binding.receiver.setText(receiver);
        binding.amount.setText(value);
    }

    @Override
    public void showProgress() {
        sweetAlertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.setTitleText(getString(R.string.connection))
                .setContentText(getString(R.string.sending))
                .show();
    }

    @Override
    public void hideProgress() {
        // nothing
    }

    @Override
    public void onNavigationItemClicked() {
        // nothing
    }
}
