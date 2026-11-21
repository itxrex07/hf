package p001hx.dash;

import android.util.Log;
import java.util.Locale;
import p001hx.util.Logs;

/* loaded from: classes3.dex */
final class TransferLog {
    private static final int BYTES_PER_KIB = 1024;
    private static final double BYTES_PER_MB = 1000000.0d;
    private static final long MS_PER_SECOND = 1000;
    private static final long NANOS_PER_MS = 1000000;
    private static final long REPORT_INTERVAL_MS = 5000;
    static final TransferLog SHARED = new TransferLog();
    private int activeRanges;
    private long bodyNanos;
    private long headerNanos;
    private long readBytes;
    private long reads;
    private long retries;
    private long served;
    private long windowStart = System.nanoTime();

    TransferLog() {
    }

    synchronized void rangeStarted() {
        int i = this.activeRanges;
        this.activeRanges = i + 1;
        if (i == 0) {
            restart(System.nanoTime());
        }
    }

    synchronized int rangeEnded() {
        int i;
        i = this.activeRanges - 1;
        this.activeRanges = i;
        return i;
    }

    synchronized void read(long j, long j2, long j3) {
        this.reads++;
        this.readBytes += j;
        this.headerNanos += j2;
        this.bodyNanos += j3;
    }

    synchronized void retry() {
        this.retries++;
    }

    synchronized void served(long j) {
        this.served += j;
        long nanoTime = System.nanoTime();
        if (nanoTime - this.windowStart >= 5000000000L) {
            report(nanoTime);
            restart(nanoTime);
        }
    }

    private void report(long j) {
        double d = (j - this.windowStart) / 1.0E9d;
        Log.i(Logs.TAG, String.format(Locale.ROOT, "DASH served %.1f MB in %.1f s = %.1f MB/s over %d ranges, %d CDN reads of %d KiB avg, headers %d ms avg, body %d ms avg, %d retries", Double.valueOf(this.served / BYTES_PER_MB), Double.valueOf(d), Double.valueOf((this.served / BYTES_PER_MB) / d), Integer.valueOf(this.activeRanges), Long.valueOf(this.reads), Long.valueOf(average(this.readBytes) / 1024), Long.valueOf(average(this.headerNanos) / NANOS_PER_MS), Long.valueOf(average(this.bodyNanos) / NANOS_PER_MS), Long.valueOf(this.retries)));
    }

    private long average(long j) {
        if (this.reads == 0) {
            return 0L;
        }
        return j / this.reads;
    }

    private void restart(long j) {
        this.windowStart = j;
        this.served = 0L;
        this.reads = 0L;
        this.readBytes = 0L;
        this.headerNanos = 0L;
        this.bodyNanos = 0L;
        this.retries = 0L;
    }
}
