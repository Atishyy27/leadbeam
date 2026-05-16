package com.fieldflow.feature.map.domain.usecase;

import com.fieldflow.feature.map.domain.repository.BusinessRepository;
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
public final class GetBusinessesInBoundsUseCase_Factory implements Factory<GetBusinessesInBoundsUseCase> {
  private final Provider<BusinessRepository> repositoryProvider;

  public GetBusinessesInBoundsUseCase_Factory(Provider<BusinessRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetBusinessesInBoundsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetBusinessesInBoundsUseCase_Factory create(
      Provider<BusinessRepository> repositoryProvider) {
    return new GetBusinessesInBoundsUseCase_Factory(repositoryProvider);
  }

  public static GetBusinessesInBoundsUseCase newInstance(BusinessRepository repository) {
    return new GetBusinessesInBoundsUseCase(repository);
  }
}
