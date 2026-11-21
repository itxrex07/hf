package p001hx.dash;

import java.io.IOException;

/* loaded from: classes3.dex */
public interface CdnCookies {
    String current() throws IOException;

    String refresh(String str) throws IOException;
}
