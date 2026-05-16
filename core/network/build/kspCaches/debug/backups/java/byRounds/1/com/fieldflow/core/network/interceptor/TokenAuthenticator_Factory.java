package com.fieldflow.core.network.interceptor;

import com.fieldflow.core.database.PreferencesManager;
import com.fieldflow.core.network.api.TokenRefreshService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
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
public final class TokenAuthenticator_Factory implements Factory<TokenAuthenticator> {
  private final Provider<PreferencesManager> preferencesManagerProvider;

  private final Provider<TokenRefreshService> tokenRefreshServiceProvider;

  public TokenAuthenticator_Factory(Provider<PreferencesManager> preferencesManagerProvider,
      Provider<TokenRefreshService> tokenRefreshServiceProvider) {
    this.preferencesManagerProvider = preferencesManagerProvider;
    this.tokenRefreshServiceProvider = tokenRefreshServiceProvider;
  }

  @Override
  public TokenAuthenticator get() {
    return newInstance(preferencesManagerProvider.get(), tokenRefreshServiceProvider.get());
  }

  public static TokenAuthenticator_Factory create(
      Provider<PreferencesManager> preferencesManagerProvider,
      Provider<TokenRefreshService> tokenRefreshServiceProvider) {
    return new TokenAuthenticator_Factory(preferencesManagerProvider, tokenRefreshServiceProvider);
  }

  public static TokenAuthenticator newInstance(PreferencesManager preferencesManager,
      TokenRefreshService tokenRefreshService) {
    return new TokenAuthenticator(preferencesManager, tokenRefreshService);
  }
}
