package com.fieldflow.core.network.di;

import com.fieldflow.core.network.api.TokenRefreshService;
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
public final class NetworkModule_ProvideTokenRefreshServiceFactory implements Factory<TokenRefreshService> {
  private final Provider<OkHttpClient> okHttpClientProvider;

  public NetworkModule_ProvideTokenRefreshServiceFactory(
      Provider<OkHttpClient> okHttpClientProvider) {
    this.okHttpClientProvider = okHttpClientProvider;
  }

  @Override
  public TokenRefreshService get() {
    return provideTokenRefreshService(okHttpClientProvider.get());
  }

  public static NetworkModule_ProvideTokenRefreshServiceFactory create(
      Provider<OkHttpClient> okHttpClientProvider) {
    return new NetworkModule_ProvideTokenRefreshServiceFactory(okHttpClientProvider);
  }

  public static TokenRefreshService provideTokenRefreshService(OkHttpClient okHttpClient) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideTokenRefreshService(okHttpClient));
  }
}
