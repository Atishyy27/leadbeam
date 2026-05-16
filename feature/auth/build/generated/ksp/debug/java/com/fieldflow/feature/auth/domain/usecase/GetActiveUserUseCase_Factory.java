package com.fieldflow.feature.auth.domain.usecase;

import com.fieldflow.feature.auth.domain.repository.AuthRepository;
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
public final class GetActiveUserUseCase_Factory implements Factory<GetActiveUserUseCase> {
  private final Provider<AuthRepository> authRepositoryProvider;

  public GetActiveUserUseCase_Factory(Provider<AuthRepository> authRepositoryProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
  }

  @Override
  public GetActiveUserUseCase get() {
    return newInstance(authRepositoryProvider.get());
  }

  public static GetActiveUserUseCase_Factory create(
      Provider<AuthRepository> authRepositoryProvider) {
    return new GetActiveUserUseCase_Factory(authRepositoryProvider);
  }

  public static GetActiveUserUseCase newInstance(AuthRepository authRepository) {
    return new GetActiveUserUseCase(authRepository);
  }
}
