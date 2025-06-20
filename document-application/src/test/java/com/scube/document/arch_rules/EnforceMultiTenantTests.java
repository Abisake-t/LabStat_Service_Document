package com.scube.document.arch_rules;

import com.scube.multi.tenant.AllMultiTenancyTests;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchTests;

@AnalyzeClasses(packages = "com.scube", importOptions = ImportOption.DoNotIncludeTests.class)
class EnforceMultiTenantTests {
    @ArchTest
    ArchTests all = ArchTests.in(AllMultiTenancyTests.class);
}