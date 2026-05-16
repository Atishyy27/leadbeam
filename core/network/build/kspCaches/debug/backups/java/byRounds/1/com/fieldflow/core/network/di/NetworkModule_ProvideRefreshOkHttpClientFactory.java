package com.fieldflow.core.network.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

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
public final class NetworkModule_ProvideRefreshOkHttpClientFactory implements Factory<OkHttpClient> {
  private final Provider<HttpLoggingInterceptor> loggingInterceptorProvider;

  public NetworkModule_ProvideRefreshOkHttpClientFactory(
      Provider<HttpLoggingInterceptor> loggingInterceptorProvider) {
    this.loggingInterceptorProvider = loggingInterceptorProvider;
  }

  @Override
  public OkHttpClient get() {
    return provideRefreshOkHttpClient(loggingInterceptorProvider.get());
  }

  public static NetworkModule_ProvideRefreshOkHttpClientFactory create(
      Provider<HttpLoggingInterceptor> loggingInterceptorProvider) {
    return new NetworkModule_ProvideRefreshOkHttpClientFactory(loggingInterceptorProvider);
  }

  public static OkHttpClient provideRefreshOkHttpClient(HttpLoggingInterceptor loggingInterceptor) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideRefreshOkHttpClient(loggingInterceptor));
  }
}
