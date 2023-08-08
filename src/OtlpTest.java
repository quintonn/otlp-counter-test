
import java.time.Duration;
import java.util.concurrent.TimeUnit;

//import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.exporter.otlp.http.logs.OtlpHttpLogRecordExporter;
import io.opentelemetry.exporter.otlp.http.metrics.OtlpHttpMetricExporter;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.SdkLoggerProviderBuilder;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.SdkMeterProviderBuilder;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;


public class OtlpTest {

    private static final Resource RESOURCE = Resource.getDefault().toBuilder()
        .put(ResourceAttributes.SERVICE_NAME, "java test")
        .build();

    SdkMeterProvider meterProvider;
    LongCounter counter;
    Meter meter;

    public void start(final String otlpEndpoint) throws InterruptedException {

        this.setup(otlpEndpoint);
    }

    public void stop() {

        this.meterProvider.shutdown().join(10, TimeUnit.SECONDS);
    }

    private void setup(final String otlpEndpoint) {

        final org.slf4j.Logger log = LoggerFactory.getLogger(OtlpTest.class);

        // Create an OTLP HTTP exporter
        final OtlpHttpLogRecordExporter exporter = OtlpHttpLogRecordExporter.builder()
            .setEndpoint(otlpEndpoint + "logs")
            .setCompression("none")
            .addHeader("q-test", "my-application")
            .build();
        final OtlpHttpMetricExporter metricExporter = OtlpHttpMetricExporter.builder()
            .setEndpoint(otlpEndpoint + "metrics")
            //.setAggregationTemporalitySelector(AggregationTemporalitySelector.deltaPreferred())
            .setCompression("none")
            .build();

        final SdkLoggerProviderBuilder logEmitterProviderBuilder = SdkLoggerProvider.builder();

        final AttributesBuilder resourceAttributes = Attributes.builder()
            .put(ResourceAttributes.SERVICE_INSTANCE_ID, "1.2.3")
            .put("service.instance.uuid", "1.2.3")
            .put("service.type", "my-test-service")
            .put("my-message-id", "10001")
            .put(ResourceAttributes.SERVICE_NAMESPACE, "my-test-service")
            .put(ResourceAttributes.SERVICE_VERSION, "1.2.3")
            .put(ResourceAttributes.TELEMETRY_SDK_NAME, "MY_TEST")
            .put(ResourceAttributes.TELEMETRY_SDK_LANGUAGE, ResourceAttributes.TelemetrySdkLanguageValues.JAVA)
            .put(ResourceAttributes.TELEMETRY_SDK_VERSION, "1.2.3")
            .build().toBuilder();

        final Resource resource = Resource.create(resourceAttributes.build());

        logEmitterProviderBuilder.setResource(resource);

        final SdkMeterProviderBuilder metricEmitterProviderBuilder = SdkMeterProvider.builder();
        metricEmitterProviderBuilder.setResource(resource);

        final PeriodicMetricReader metricReader = PeriodicMetricReader.builder(metricExporter).setInterval(Duration.ofSeconds(Math.max(30, 50))).build();
        final SdkMeterProviderBuilder metricProviderBuilder = metricEmitterProviderBuilder.registerMetricReader(metricReader);

        this.meterProvider = metricProviderBuilder.build();
        this.meter = this.meterProvider.meterBuilder("my-test").build();
        this.counter = this.meter.counterBuilder("java_counter_9").setUnit("message").build();
    }

    public void addCounter(final long userValue) throws InterruptedException {

        for (int i = 0; i < 1; i++) {

            final long value = (long) ((Math.random() * ((5 - 0) + 1)) + 0);

            final AttributesBuilder attributes = Attributes.builder()
                .put("test-name", "test-01")
                .build().toBuilder();

            this.counter.add(userValue);//, attributes.build());

            System.out.println("sending " + userValue);
        }
    }
}
