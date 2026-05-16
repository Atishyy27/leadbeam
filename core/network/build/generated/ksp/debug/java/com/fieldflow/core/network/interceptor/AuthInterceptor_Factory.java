package com.fieldflow.core.network.interceptor;

import com.fieldflow.core.database.PreferencesManager;
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
public final class AuthInterceptor_Factory implements Factory<AuthInterceptor> {
  private final Provider<PreferencesManager> preferencesManagerProvider;

  public AuthInterceptor_Factory(Provider<PreferencesManager> preferencesManagerProvider) {
    this.preferencesManagerProvider = preferencesManagerProvider;
  }

  @Override
  public AuthInterceptor get() {
    return newInstance(preferencesManagerProvider.get());
  }

  public static AuthInterceptor_Factory create(
      Provider<PreferencesManager> preferencesManagerProvider) {
    return new AuthInterceptor_Factory(preferencesManagerProvider);
  }

  public static AuthInterceptor newInstance(PreferencesManager preferencesManager) {
    return new AuthInterceptor(preferencesManager);
  }
}
