/*
 * Copyright 2013-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.facebook.buck.android;

import com.facebook.buck.java.AccumulateClassNames;
import com.facebook.buck.rules.AbiRule;
import com.facebook.buck.rules.AbstractBuildRuleBuilder;
import com.facebook.buck.rules.AbstractBuildRuleBuilderParams;
import com.facebook.buck.rules.AbstractCachingBuildRule;
import com.facebook.buck.rules.BuildRuleParams;
import com.facebook.buck.rules.BuildRuleResolver;
import com.facebook.buck.rules.BuildRuleType;
import com.facebook.buck.rules.Buildable;
import com.facebook.buck.rules.Sha1HashCode;
import com.google.common.base.Preconditions;

public class IntermediateDexRule extends AbstractCachingBuildRule implements AbiRule {

  private final DexProducedFromJavaLibraryThatContainsClassFiles buildable;

  IntermediateDexRule(DexProducedFromJavaLibraryThatContainsClassFiles buildable,
      BuildRuleParams params) {
    super(buildable, params);
    this.buildable = Preconditions.checkNotNull(buildable);
  }

  @Override
  public Buildable getBuildable() {
    return buildable;
  }

  @Override
  public BuildRuleType getType() {
    return BuildRuleType._PRE_DEX;
  }

  /**
   * The ABI key for the deps of this rule should be a hash of the classes.txt file produced by
   * {@link AccumulateClassNames}.
   */
  @Override
  public Sha1HashCode getAbiKeyForDeps() {
    return buildable.getAbiKeyForDeps();
  }

  public static Builder newPreDexBuilder(AbstractBuildRuleBuilderParams params) {
    return new Builder(params);
  }

  public static class Builder extends AbstractBuildRuleBuilder<IntermediateDexRule> {

    private AccumulateClassNames javaLibraryWithClassesList;

    protected Builder(AbstractBuildRuleBuilderParams params) {
      super(params);
    }

    @Override
    public IntermediateDexRule build(BuildRuleResolver ruleResolver) {
      DexProducedFromJavaLibraryThatContainsClassFiles buildable =
          new DexProducedFromJavaLibraryThatContainsClassFiles(getBuildTarget(),
              javaLibraryWithClassesList);
      return new IntermediateDexRule(buildable,
          createBuildRuleParams(ruleResolver));
    }

    public Builder setAccumulateClassNamesDep(AccumulateClassNames javaLibraryWithClassesList) {
      this.javaLibraryWithClassesList = javaLibraryWithClassesList;
      return this;
    }
  }
}
