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

package click.kobaken.rxirohaandroid.repository;

import java.util.List;

import click.kobaken.rxirohaandroid.model.Domain;
import click.kobaken.rxirohaandroid.net.dataset.reqest.DomainRegisterRequest;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface DomainRepository {
    @POST("/domain/register")
    Observable<Domain> register(@Body DomainRegisterRequest body);

    @GET("/domain/list")
    Observable<List<Domain>> findDomains(@Query("limit") int limit, @Query("offset") int offset);
}
