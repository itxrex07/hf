package p001hx.loopback;

import java.io.IOException;
import p001hx.dash.DashFile;

/* loaded from: classes3.dex */
public interface DashFileSource {
    DashFile open(String str, int i, int i2, int i3, String str2, long j, String str3, boolean z) throws IOException;
}
