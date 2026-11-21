package com.android.tools.p000r8.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* compiled from: D8$$SyntheticClass */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: classes3.dex */
public /* synthetic */ @interface LambdaMethod {
    String holder();

    String method();

    String proto();
}
