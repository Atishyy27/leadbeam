package com.fieldflow;

import com.fieldflow.feature.auth.domain.usecase.CheckSessionUseCase;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<CheckSessionUseCase> checkSessionUseCaseProvider;

  public MainActivity_MembersInjector(Provider<CheckSessionUseCase> checkSessionUseCaseProvider) {
    this.checkSessionUseCaseProvider = checkSessionUseCaseProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<CheckSessionUseCase> checkSessionUseCaseProvider) {
    return new MainActivity_MembersInjector(checkSessionUseCaseProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectCheckSessionUseCase(instance, checkSessionUseCaseProvider.get());
  }

  @InjectedFieldSignature("com.fieldflow.MainActivity.checkSessionUseCase")
  public static void injectCheckSessionUseCase(MainActivity instance,
      CheckSessionUseCase checkSessionUseCase) {
    instance.checkSessionUseCase = checkSessionUseCase;
  }
}
