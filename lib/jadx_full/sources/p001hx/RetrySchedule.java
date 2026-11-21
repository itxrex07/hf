package p001hx;

/* loaded from: classes3.dex */
final class RetrySchedule {
    static final long STOP = -1;
    private final int fastAttempts;
    private final long fastDelayMs;
    private final int slowAttempts;
    private final long slowDelayMs;

    RetrySchedule(int i, long j, int i2, long j2) {
        this.fastAttempts = i;
        this.fastDelayMs = j;
        this.slowAttempts = i2;
        this.slowDelayMs = j2;
    }

    long delayAfter(int i) {
        if (i < this.fastAttempts) {
            return this.fastDelayMs;
        }
        return i < this.fastAttempts + this.slowAttempts ? this.slowDelayMs : STOP;
    }

    boolean reportsAfter(int i) {
        return i == this.fastAttempts;
    }
}
