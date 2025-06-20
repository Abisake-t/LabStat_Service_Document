package com.scube.document.arch_rules;

import com.scube.lib.misc.validation.AllValidationRuleTests;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchTests;

@AnalyzeClasses(packages = "com.scube", importOptions = ImportOption.DoNotIncludeTests.class)
class EnforceValidationRuleTests {
    @ArchTest
    ArchTests all = ArchTests.in(AllValidationRuleTests.class);
}