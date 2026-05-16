package com.fieldflow.core.network.di;

import com.fieldflow.core.network.api.ApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("javax.inject.Named")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class NetworkModule_ProvideApiServiceFactory implements Factory<ApiService> {
  private final Provider<OkHttpClient> okHttpClientProvider;

  public NetworkModule_ProvideApiServiceFactory(Provider<OkHttpClient> okHttpClientProvider) {
    this.okHttpClientProvider = okHttpClientProvider;
  }

  @Override
  public ApiService get() {
    return provideApiService(okHttpClientProvider.get());
  }

  public static NetworkModule_ProvideApiServiceFactory create(
      Provider<OkHttpClient> okHttpClientProvider) {
    return new NetworkModule_ProvideApiServiceFactory(okHttpClientProvider);
  }

  public static ApiService provideApiService(OkHttpClient okHttpClient) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideApiService(okHttpClient));
  }
}
