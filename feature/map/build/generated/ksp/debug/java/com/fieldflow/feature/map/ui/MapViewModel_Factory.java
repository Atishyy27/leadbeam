package com.fieldflow.feature.map.ui;

import com.fieldflow.feature.map.domain.usecase.GetBusinessesInBoundsUseCase;
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
public final class MapViewModel_Factory implements Factory<MapViewModel> {
  private final Provider<GetBusinessesInBoundsUseCase> getBusinessesInBoundsUseCaseProvider;

  public MapViewModel_Factory(
      Provider<GetBusinessesInBoundsUseCase> getBusinessesInBoundsUseCaseProvider) {
    this.getBusinessesInBoundsUseCaseProvider = getBusinessesInBoundsUseCaseProvider;
  }

  @Override
  public MapViewModel get() {
    return newInstance(getBusinessesInBoundsUseCaseProvider.get());
  }

  public static MapViewModel_Factory create(
      Provider<GetBusinessesInBoundsUseCase> getBusinessesInBoundsUseCaseProvider) {
    return new MapViewModel_Factory(getBusinessesInBoundsUseCaseProvider);
  }

  public static MapViewModel newInstance(
      GetBusinessesInBoundsUseCase getBusinessesInBoundsUseCase) {
    return new MapViewModel(getBusinessesInBoundsUseCase);
  }
}
