package com.fieldflow.feature.map.data.repository;

import com.fieldflow.core.database.dao.BusinessDao;
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
public final class BusinessRepositoryImpl_Factory implements Factory<BusinessRepositoryImpl> {
  private final Provider<BusinessDao> businessDaoProvider;

  private final Provider<ApiService> apiServiceProvider;

  public BusinessRepositoryImpl_Factory(Provider<BusinessDao> businessDaoProvider,
      Provider<ApiService> apiServiceProvider) {
    this.businessDaoProvider = businessDaoProvider;
    this.apiServiceProvider = apiServiceProvider;
  }

  @Override
  public BusinessRepositoryImpl get() {
    return newInstance(businessDaoProvider.get(), apiServiceProvider.get());
  }

  public static BusinessRepositoryImpl_Factory create(Provider<BusinessDao> businessDaoProvider,
      Provider<ApiService> apiServiceProvider) {
    return new BusinessRepositoryImpl_Factory(businessDaoProvider, apiServiceProvider);
  }

  public static BusinessRepositoryImpl newInstance(BusinessDao businessDao, ApiService apiService) {
    return new BusinessRepositoryImpl(businessDao, apiService);
  }
}
