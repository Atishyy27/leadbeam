package com.fieldflow.core.database.di;

import com.fieldflow.core.database.FieldFlowDatabase;
import com.fieldflow.core.database.dao.BusinessDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideBusinessDaoFactory implements Factory<BusinessDao> {
  private final Provider<FieldFlowDatabase> databaseProvider;

  public DatabaseModule_ProvideBusinessDaoFactory(Provider<FieldFlowDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public BusinessDao get() {
    return provideBusinessDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideBusinessDaoFactory create(
      Provider<FieldFlowDatabase> databaseProvider) {
    return new DatabaseModule_ProvideBusinessDaoFactory(databaseProvider);
  }

  public static BusinessDao provideBusinessDao(FieldFlowDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideBusinessDao(database));
  }
}
