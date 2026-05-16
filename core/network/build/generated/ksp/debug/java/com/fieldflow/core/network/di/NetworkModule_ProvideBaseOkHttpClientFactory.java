package com.fieldflow.core.network.di;

import com.fieldflow.core.network.interceptor.AuthInterceptor;
import com.fieldflow.core.network.interceptor.TokenAuthenticator;
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
public final class NetworkModule_ProvideBaseOkHttpClientFactory implements Factory<OkHttpClient> {
  private final Provider<AuthInterceptor> authInterceptorProvider;

  private final Provider<TokenAuthenticator> tokenAuthenticatorProvider;

  private final Provider<HttpLoggingInterceptor> loggingInterceptorProvider;

  public NetworkModule_ProvideBaseOkHttpClientFactory(
      Provider<AuthInterceptor> authInterceptorProvider,
      Provider<TokenAuthenticator> tokenAuthenticatorProvider,
      Provider<HttpLoggingInterceptor> loggingInterceptorProvider) {
    this.authInterceptorProvider = authInterceptorProvider;
    this.tokenAuthenticatorProvider = tokenAuthenticatorProvider;
    this.loggingInterceptorProvider = loggingInterceptorProvider;
  }

  @Override
  public OkHttpClient get() {
    return provideBaseOkHttpClient(authInterceptorProvider.get(), tokenAuthenticatorProvider.get(), loggingInterceptorProvider.get());
  }

  public static NetworkModule_ProvideBaseOkHttpClientFactory create(
      Provider<AuthInterceptor> authInterceptorProvider,
      Provider<TokenAuthenticator> tokenAuthenticatorProvider,
      Provider<HttpLoggingInterceptor> loggingInterceptorProvider) {
    return new NetworkModule_ProvideBaseOkHttpClientFactory(authInterceptorProvider, tokenAuthenticatorProvider, loggingInterceptorProvider);
  }

  public static OkHttpClient provideBaseOkHttpClient(AuthInterceptor authInterceptor,
      TokenAuthenticator tokenAuthenticator, HttpLoggingInterceptor loggingInterceptor) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideBaseOkHttpClient(authInterceptor, tokenAuthenticator, loggingInterceptor));
  }
}
