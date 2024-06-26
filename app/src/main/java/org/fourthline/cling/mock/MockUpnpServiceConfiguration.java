package org.fourthline.cling.mock;

import org.fourthline.cling.DefaultUpnpServiceConfiguration;
import org.fourthline.cling.transport.impl.NetworkAddressFactoryImpl;
import org.fourthline.cling.transport.spi.NetworkAddressFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.enterprise.inject.Alternative;

@Alternative
public class MockUpnpServiceConfiguration extends DefaultUpnpServiceConfiguration {
    protected final boolean maintainsRegistry;
    protected final boolean multiThreaded;

    public MockUpnpServiceConfiguration() {
        this(false, false);
    }

    public MockUpnpServiceConfiguration(boolean z) {
        this(z, false);
    }

    public MockUpnpServiceConfiguration(boolean z, boolean z2) {
        super(false);
        this.maintainsRegistry = z;
        this.multiThreaded = z2;
    }

    public boolean isMaintainsRegistry() {
        return this.maintainsRegistry;
    }

    public boolean isMultiThreaded() {
        return this.multiThreaded;
    }

    @Override
    protected NetworkAddressFactory createNetworkAddressFactory(int i) {
        return new NetworkAddressFactoryImpl(i) {
            @Override
            protected boolean isUsableNetworkInterface(NetworkInterface networkInterface) throws Exception {
                return networkInterface.isLoopback();
            }

            
            @Override
            public boolean isUsableAddress(NetworkInterface networkInterface, InetAddress inetAddress) {
                return inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address);
            }
        };
    }

    @Override
    public Executor getRegistryMaintainerExecutor() {
        if (isMaintainsRegistry()) {
            return new Executor() {
                @Override
                public void execute(Runnable runnable) {
                    new Thread(runnable).start();
                }
            };
        }
        return getDefaultExecutorService();
    }

    
    @Override
    public ExecutorService getDefaultExecutorService() {
        if (isMultiThreaded()) {
            return super.getDefaultExecutorService();
        }
        return new AbstractExecutorService() {
            boolean terminated;

            @Override
            public void shutdown() {
                this.terminated = true;
            }

            @Override
            public List<Runnable> shutdownNow() {
                shutdown();
                return null;
            }

            @Override
            public boolean isShutdown() {
                return this.terminated;
            }

            @Override
            public boolean isTerminated() {
                return this.terminated;
            }

            @Override
            public boolean awaitTermination(long j, TimeUnit timeUnit) throws InterruptedException {
                shutdown();
                return this.terminated;
            }

            @Override
            public void execute(Runnable runnable) {
                runnable.run();
            }
        };
    }
}
