package kieker.monitoring.probe.sigar;

import kieker.monitoring.probe.sigar.samplers.AbstractSigarSampler;
import kieker.monitoring.probe.sigar.samplers.CPUsCombinedPercSampler;
import kieker.monitoring.probe.sigar.samplers.CPUsDetailedPercSampler;
import kieker.monitoring.probe.sigar.samplers.MemSwapUsageSampler;

import org.hyperic.sigar.Humidor;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarProxy;

/**
 * Provides factory methods for {@link AbstractSigarSampler}s.
 *
 * @author Andre van Hoorn
 */
public final class SigarSamplerFactory implements ISigarSamplerFactory {
        //private static final Log log = LogFactory.getLog(SigarSamplerFactory.class);

        /**
         * Returns the singleton instance of the {@link SigarSamplerFactory}.
         */
        public final static SigarSamplerFactory getInstance() {
                return LazyHolder.INSTANCE;
        }

        /**
         * {@link SigarProxy} instance used to retrieve the data to be logged.
         */
        private final SigarProxy sigar;

        /**
         * {@link SigarProxy} instance used by this {@link SigarSamplerFactory}.
         *
         * @return the sigar
         */
        public final SigarProxy getSigar() {
                return this.sigar;
        }

        /**
         * Used by {@link #getInstance()} to construct the singleton instance.
         */
        private SigarSamplerFactory() {
                this(new Humidor(new Sigar()));
        }

        /**
         * Constructs a {@link SigarSamplerFactory} with the given parameters.
         *
         * @param humidor
         */
        public SigarSamplerFactory(final Humidor humidor) {
                this.sigar = humidor.getSigar();
        }

        @Override
        public CPUsCombinedPercSampler createSensorCPUsCombinedPerc() {
                return new CPUsCombinedPercSampler(this.sigar);
        }

        @Override
        public CPUsDetailedPercSampler createSensorCPUsDetailedPerc() {
                return new CPUsDetailedPercSampler(this.sigar);
        }

        @Override
        public MemSwapUsageSampler createSensorMemSwapUsage() {
                return new MemSwapUsageSampler(this.sigar);
        }
       
        /**
         * SINGLETON
         */
        private final static class LazyHolder {
                private static final SigarSamplerFactory INSTANCE = new SigarSamplerFactory();
        }
}