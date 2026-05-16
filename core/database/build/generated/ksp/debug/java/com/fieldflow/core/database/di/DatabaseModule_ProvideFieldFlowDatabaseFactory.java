package com.fieldflow.core.database.di;

import android.content.Context;
import com.fieldflow.core.database.FieldFlowDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class DatabaseModule_ProvideFieldFlowDatabaseFactory implements Factory<FieldFlowDatabase> {
  private final Provider<Context> contextProvider;

  public DatabaseModule_ProvideFieldFlowDatabaseFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public FieldFlowDatabase get() {
    return provideFieldFlowDatabase(contextProvider.get());
  }

  public static DatabaseModule_ProvideFieldFlowDatabaseFactory create(
      Provider<Context> contextProvider) {
    return new DatabaseModule_ProvideFieldFlowDatabaseFactory(contextProvider);
  }

  public static FieldFlowDatabase provideFieldFlowDatabase(Context context) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideFieldFlowDatabase(context));
  }
}
