# RxIrohaAndroid


![build status](https://circleci.com/gh/kobaken0029/RxIrohaAndroid.png?circle-token=:circle-token=c3977114ce208efbb8bddd378d974b97e942662)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/428a3bd7a8b947e2bc3bcf72b53ca9c2)](https://www.codacy.com/app/kobaken0029/RxIrohaAndroid?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=kobaken0029/RxIrohaAndroid&amp;utm_campaign=Badge_Grade)

## About kobaken0029's FORK
This github project represents the RxIrohaAndroid development stream. The earlier project page (hyperledger repository) represents the earlier iroha-android development stream. This versions have benefitted from strong involvement from individual for @kobaken0029.

iroha-android is currently in active development, primarily internally at hyperledger project, with regular pushes to the open-source community.

## What is いろは(iroha)?
いろは(iroha) is [this](https://github.com/hyperledger/iroha).

## Description
いろはAndroid(RxIrohaAndroid) is client library for using いろは(iroha) for Android.


- [Requirements](#requirements)
- [Installation](#installation)
- [Usage](#usage)
- [License](#license)

## Requirement

- Android Studio >=2.2.1  
- Android API Level >=v19 (v4.4, KitKat)
- Android Build Tools >=v25  

## Installation

### Gradle
In your ```app/build.gradle```   

```gradle
compile 'click.kobaken:rx-iroha-android:0.3.3'
```

### Maven
Or if you use Maven, like this

```maven
<dependency>
  <groupId>click.kobaken</groupId>
  <artifactId>rx-iroha-android</artifactId>
  <version>0.3.3</version>
  <type>pom</type>
</dependency>
```


## Usage
### API

### click.kobaken.rxirohaandroid.security.MessageDigest
#### digest
```java
String hashedMessage = MessageDigest.digest("message", MessageDigest.SHA3_256);
// ===> hashed message by SHA3_256
```

### click.kobaken.rxirohaandroid.Iroha
#### Initialize
In your app
on 'onCreate' your class inheriting Application

``` java
import click.kobaken.rxirohaandroid.Iroha;

new Iroha.Builder()
        .baseUrl("https://[input your domain(base url)]")
        .client(IrohaHttpClient.getInstance().get())
        .build();
```

#### createKeyPair
```java
import io.soramitsu.irohaandroid.Iroha;
import io.soramitsu.irohaandroid.model.KeyPair;

KeyPair keypair = Iroha.createKeyPair();
keypair.publicKey; // Ed25519 public key encoded by base64
keypair.privateKey; // Ed25519 private key encoded by base64
```

#### sign
```java
import io.soramitsu.irohaandroid.Iroha;
import io.soramitsu.irohaandroid.model.KeyPair;

String signature = Iroha.sign(keyPair, "message")
//===> signature // String
```

#### verify
```java
import io.soramitsu.irohaandroid.Iroha;
import io.soramitsu.irohaandroid.model.KeyPair;

boolean verify = Iroha.verify(keyPair.publicKey, signature, "message")
//===> true if the correct message
```

### Web API
#### registerAccount
```java
Iroha.getInstance().registerAccount("publicKey", "alias");
// ===> return Observable<Account> for register account
```

#### findAccount
```java
Iroha.getInstance().findAccount("uuid");
// ===> return Observable<Account> for find account
```

#### registerDomain
```java
Iroha.getInstance().registerDomain("name", "owner", "signature");
// ===> return Observable<Doamin> for register domain
```

#### findDomains
```java
Iroha.getInstance().findDomains(/* limit */30, /* offset */0);
// ===> return Observable<List<Domain>> for find domains
```

#### createAsset
```java
Iroha.getInstance().createAsset("name", "owner", "creator", "signature");
// ===> return Observable<Asset> for create asset
```

#### findAssets
```java
Iroha.getInstance().findAssets("domain", /* limit */30, /* offset */0);
// ===> return Observable<List<Asset>> for find assets
```

#### operationAsset
```java
Iroha.getInstance().operationAsset(
            "asset-uuid",
            "command",
            "amount",
            "sender",
            "receiver",
            "signature",
            /* timestamp */100000
);
// ===> return Observable<BaseModel> for operation
```

#### findTransactionHistory
```java
// Single asset
Iroha.getInstance().findTransactionHistory("uuid", /* limit */30, /* offset */0);
// ===> return Observable<TraqnsactionHistory> for find transaction history

// Multi assets
Iroha.getInstance().findTransactionHistory("domain", "asset-uuid", "uuid",  /* limit */30, /* offset */0);
// ===> return Observable<TransactionHistory> for find transaction history
```

### Usage example
You can use like RxJava2.

ex.) registerAccount
```java
Iroha.getInstance().registerAccount(keyPair.publicKey, alias)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribeWith(new DisposableObserver<Account>() {
            @Override
            public void onNext(Account result) {
                // successful!
            }

            @Override
            public void onError(Throwable e) {
                // error!
            }

            @Override
            public void onComplete() {
                // complete!
            }
        });
);
```

ex.) findAccount and findTransactionHistory
```java
Observable.zip(iroha.findAccount(uuid), iroha.findTransactionHistory(uuid, 30, 0),
        new BiFunction<Account, TransactionHistory, Tx>() {
            @Override
            public Tx apply(Account account,TransactionHistory transactionHistory) throws Exception {
                Tx history = new Tx();
                if (account != null && account.assets != null && !account.assets.isEmpty()) {
                    history.value = account.assets.get(0).value;
                }
                history.histories = transactionHistory.history;
                return history;
            }
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribeWith(new DisposableObserver<Tx>() {
            @Override
            public void onNext(Tx result) {
                // successful!
            }

            @Override
            public void onError(Throwable e) {
                // error!
            }

            @Override
            public void onComplete() {
                // complete!
            }
        });
```


## Author
[kobaken0029](https://github.com/kobaken0029)

## License

Copyright(c) 2016 kobaken0029

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
