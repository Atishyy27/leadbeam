package com.fieldflow.feature.auth.data.repository;

import com.fieldflow.core.database.PreferencesManager;
import com.fieldflow.core.database.dao.UserDao;
import com.fieldflow.core.network.api.ApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AuthRepositoryImpl_Factory implements Factory<AuthRepositoryImpl> {
  private final Provider<ApiService> apiServiceProvider;

  private final Provider<UserDao> userDaoProvider;

  private final Provider<PreferencesManager> preferencesManagerProvider;

  public AuthRepositoryImpl_Factory(Provider<ApiService> apiServiceProvider,
      Provider<UserDao> userDaoProvider, Provider<PreferencesManager> preferencesManagerProvider) {
    this.apiServiceProvider = apiServiceProvider;
    this.userDaoProvider = userDaoProvider;
    this.preferencesManagerProvider = preferencesManagerProvider;
  }

  @Override
  public AuthRepositoryImpl get() {
    return newInstance(apiServiceProvider.get(), userDaoProvider.get(), preferencesManagerProvider.get());
  }

  public static AuthRepositoryImpl_Factory create(Provider<ApiService> apiServiceProvider,
      Provider<UserDao> userDaoProvider, Provider<PreferencesManager> preferencesManagerProvider) {
    return new AuthRepositoryImpl_Factory(apiServiceProvider, userDaoProvider, preferencesManagerProvider);
  }

  public static AuthRepositoryImpl newInstance(ApiService apiService, UserDao userDao,
      PreferencesManager preferencesManager) {
    return new AuthRepositoryImpl(apiService, userDao, preferencesManager);
  }
}
