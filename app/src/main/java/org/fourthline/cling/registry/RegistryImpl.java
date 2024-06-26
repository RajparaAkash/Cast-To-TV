package org.fourthline.cling.registry;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.model.DiscoveryOptions;
import org.fourthline.cling.model.ServiceReference;
import org.fourthline.cling.model.gena.LocalGENASubscription;
import org.fourthline.cling.model.gena.RemoteGENASubscription;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteDeviceIdentity;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.resource.Resource;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.protocol.ProtocolFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped

public class RegistryImpl implements Registry {
    private static Logger log = Logger.getLogger(Registry.class.getName());
    protected RegistryMaintainer registryMaintainer;
    protected UpnpService upnpService;
    protected final Set<RemoteGENASubscription> pendingSubscriptionsLock = new HashSet();
    protected final Set<RegistryListener> registryListeners = new HashSet();
    protected final Set<RegistryItem<URI, Resource>> resourceItems = new HashSet();
    protected final List<Runnable> pendingExecutions = new ArrayList();
    protected final RemoteItems remoteItems = new RemoteItems(this);
    protected final LocalItems localItems = new LocalItems(this);

    public RegistryImpl() {
    }

    @Inject
    public RegistryImpl(UpnpService upnpService) {
        Logger logger = log;
        logger.fine("Creating Registry: " + getClass().getName());
        this.upnpService = upnpService;
        log.fine("Starting registry background maintenance...");
        RegistryMaintainer createRegistryMaintainer = createRegistryMaintainer();
        this.registryMaintainer = createRegistryMaintainer;
        if (createRegistryMaintainer != null) {
            getConfiguration().getRegistryMaintainerExecutor().execute(this.registryMaintainer);
        }
    }

    @Override
    public UpnpService getUpnpService() {
        return this.upnpService;
    }

    @Override
    public UpnpServiceConfiguration getConfiguration() {
        return getUpnpService().getConfiguration();
    }

    @Override
    public ProtocolFactory getProtocolFactory() {
        return getUpnpService().getProtocolFactory();
    }

    
    public RegistryMaintainer createRegistryMaintainer() {
        return new RegistryMaintainer(this, getConfiguration().getRegistryMaintenanceIntervalMillis());
    }

    @Override
    public synchronized void addListener(RegistryListener registryListener) {
        this.registryListeners.add(registryListener);
    }

    @Override
    public synchronized void removeListener(RegistryListener registryListener) {
        this.registryListeners.remove(registryListener);
    }

    @Override
    public synchronized Collection<RegistryListener> getListeners() {
        return Collections.unmodifiableCollection(this.registryListeners);
    }

    @Override
    public synchronized boolean notifyDiscoveryStart(final RemoteDevice remoteDevice) {
        if (getUpnpService().getRegistry().getRemoteDevice(remoteDevice.getIdentity().getUdn(), true) != null) {
            Logger logger = log;
            logger.finer("Not notifying listeners, already registered: " + remoteDevice);
            return false;
        }
        for (final RegistryListener registryListener : getListeners()) {
            getConfiguration().getRegistryListenerExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    registryListener.remoteDeviceDiscoveryStarted(RegistryImpl.this, remoteDevice);
                }
            });
        }
        return true;
    }

    @Override
    public synchronized void notifyDiscoveryFailure(final RemoteDevice remoteDevice, final Exception exc) {
        for (final RegistryListener registryListener : getListeners()) {
            getConfiguration().getRegistryListenerExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    registryListener.remoteDeviceDiscoveryFailed(RegistryImpl.this, remoteDevice, exc);
                }
            });
        }
    }

    @Override
    public synchronized void addDevice(LocalDevice localDevice) {
        this.localItems.add(localDevice);
    }

    @Override
    public synchronized void addDevice(LocalDevice localDevice, DiscoveryOptions discoveryOptions) {
        this.localItems.add(localDevice, discoveryOptions);
    }

    @Override
    public synchronized void setDiscoveryOptions(UDN udn, DiscoveryOptions discoveryOptions) {
        this.localItems.setDiscoveryOptions(udn, discoveryOptions);
    }

    @Override
    public synchronized DiscoveryOptions getDiscoveryOptions(UDN udn) {
        return this.localItems.getDiscoveryOptions(udn);
    }

    @Override
    public synchronized void addDevice(RemoteDevice remoteDevice) {
        this.remoteItems.add(remoteDevice);
    }

    @Override
    public synchronized boolean update(RemoteDeviceIdentity remoteDeviceIdentity) {
        return this.remoteItems.update(remoteDeviceIdentity);
    }

    @Override
    public synchronized boolean removeDevice(LocalDevice localDevice) {
        return this.localItems.remove(localDevice);
    }

    @Override
    public synchronized boolean removeDevice(RemoteDevice remoteDevice) {
        return this.remoteItems.remove(remoteDevice);
    }

    @Override
    public synchronized void removeAllLocalDevices() {
        this.localItems.removeAll();
    }

    @Override
    public synchronized void removeAllRemoteDevices() {
        this.remoteItems.removeAll();
    }

    @Override
    public synchronized boolean removeDevice(UDN udn) {
        Device device = getDevice(udn, true);
        if (device != null && (device instanceof LocalDevice)) {
            return removeDevice((LocalDevice) device);
        } else if (device == null || !(device instanceof RemoteDevice)) {
            return false;
        } else {
            return removeDevice((RemoteDevice) device);
        }
    }

    @Override
    public synchronized Device getDevice(UDN udn, boolean z) {
        LocalDevice localDevice = this.localItems.get(udn, z);
        if (localDevice != null) {
            return localDevice;
        }
        RemoteDevice remoteDevice = this.remoteItems.get(udn, z);
        if (remoteDevice != null) {
            return remoteDevice;
        }
        return null;
    }

    @Override
    public synchronized LocalDevice getLocalDevice(UDN udn, boolean z) {
        return this.localItems.get(udn, z);
    }

    @Override
    public synchronized RemoteDevice getRemoteDevice(UDN udn, boolean z) {
        return this.remoteItems.get(udn, z);
    }

    @Override
    public synchronized Collection<LocalDevice> getLocalDevices() {
        return Collections.unmodifiableCollection(this.localItems.get());
    }

    @Override
    public synchronized Collection<RemoteDevice> getRemoteDevices() {
        return Collections.unmodifiableCollection(this.remoteItems.get());
    }

    @Override
    public synchronized Collection<Device> getDevices() {
        HashSet hashSet;
        hashSet = new HashSet();
        hashSet.addAll(this.localItems.get());
        hashSet.addAll(this.remoteItems.get());
        return Collections.unmodifiableCollection(hashSet);
    }

    @Override
    public synchronized Collection<Device> getDevices(DeviceType deviceType) {
        HashSet hashSet;
        hashSet = new HashSet();
        hashSet.addAll(this.localItems.get(deviceType));
        hashSet.addAll(this.remoteItems.get(deviceType));
        return Collections.unmodifiableCollection(hashSet);
    }

    @Override
    public synchronized Collection<Device> getDevices(ServiceType serviceType) {
        HashSet hashSet;
        hashSet = new HashSet();
        hashSet.addAll(this.localItems.get(serviceType));
        hashSet.addAll(this.remoteItems.get(serviceType));
        return Collections.unmodifiableCollection(hashSet);
    }

    @Override
    public synchronized Service getService(ServiceReference serviceReference) {
        Device device = getDevice(serviceReference.getUdn(), false);
        if (device != null) {
            return device.findService(serviceReference.getServiceId());
        }
        return null;
    }

    @Override
    public synchronized Resource getResource(URI uri) throws IllegalArgumentException {
        if (uri.isAbsolute()) {
            throw new IllegalArgumentException("Resource URI can not be absolute, only path and query:" + uri);
        }
        for (RegistryItem<URI, Resource> registryItem : this.resourceItems) {
            Resource item = registryItem.getItem();
            if (item.matches(uri)) {
                return item;
            }
        }
        if (uri.getPath().endsWith("/")) {
            URI create = URI.create(uri.toString().substring(0, uri.toString().length() - 1));
            for (RegistryItem<URI, Resource> registryItem2 : this.resourceItems) {
                Resource item2 = registryItem2.getItem();
                if (item2.matches(create)) {
                    return item2;
                }
            }
        }
        return null;
    }

    @Override
    public synchronized <T extends Resource> T getResource(Class<T> cls, URI uri) throws IllegalArgumentException {
        T t = (T) getResource(uri);
        if (t != null) {
            if (cls.isAssignableFrom(t.getClass())) {
                return t;
            }
        }
        return null;
    }

    @Override
    public synchronized Collection<Resource> getResources() {
        HashSet hashSet;
        hashSet = new HashSet();
        for (RegistryItem<URI, Resource> registryItem : this.resourceItems) {
            hashSet.add(registryItem.getItem());
        }
        return hashSet;
    }

    @Override
    public synchronized <T extends Resource> Collection<T> getResources(Class<T> cls) {
        HashSet hashSet;
        hashSet = new HashSet();
        for (RegistryItem<URI, Resource> registryItem : this.resourceItems) {
            if (cls.isAssignableFrom(registryItem.getItem().getClass())) {
                hashSet.add(registryItem.getItem());
            }
        }
        return hashSet;
    }

    @Override
    public synchronized void addResource(Resource resource) {
        addResource(resource, 0);
    }

    @Override
    public synchronized void addResource(Resource resource, int i) {
        RegistryItem<URI, Resource> registryItem = new RegistryItem<>(resource.getPathQuery(), resource, i);
        this.resourceItems.remove(registryItem);
        this.resourceItems.add(registryItem);
    }

    @Override
    public synchronized boolean removeResource(Resource resource) {
        return this.resourceItems.remove(new RegistryItem(resource.getPathQuery()));
    }

    @Override
    public synchronized void addLocalSubscription(LocalGENASubscription localGENASubscription) {
        this.localItems.addSubscription(localGENASubscription);
    }

    @Override
    public synchronized LocalGENASubscription getLocalSubscription(String str) {
        return this.localItems.getSubscription(str);
    }

    @Override
    public synchronized boolean updateLocalSubscription(LocalGENASubscription localGENASubscription) {
        return this.localItems.updateSubscription(localGENASubscription);
    }

    @Override
    public synchronized boolean removeLocalSubscription(LocalGENASubscription localGENASubscription) {
        return this.localItems.removeSubscription(localGENASubscription);
    }

    @Override
    public synchronized void addRemoteSubscription(RemoteGENASubscription remoteGENASubscription) {
        this.remoteItems.addSubscription(remoteGENASubscription);
    }

    @Override
    public synchronized RemoteGENASubscription getRemoteSubscription(String str) {
        return this.remoteItems.getSubscription(str);
    }

    @Override
    public synchronized void updateRemoteSubscription(RemoteGENASubscription remoteGENASubscription) {
        this.remoteItems.updateSubscription(remoteGENASubscription);
    }

    @Override
    public synchronized void removeRemoteSubscription(RemoteGENASubscription remoteGENASubscription) {
        this.remoteItems.removeSubscription(remoteGENASubscription);
    }

    @Override
    public synchronized void advertiseLocalDevices() {
        this.localItems.advertiseLocalDevices();
    }

    @Override
    public synchronized void shutdown() {
        log.fine("Shutting down registry...");
        RegistryMaintainer registryMaintainer = this.registryMaintainer;
        if (registryMaintainer != null) {
            registryMaintainer.stop();
        }
        log.finest("Executing final pending operations on shutdown: " + this.pendingExecutions.size());
        runPendingExecutions(false);
        for (RegistryListener registryListener : this.registryListeners) {
            registryListener.beforeShutdown(this);
        }
        Set<RegistryItem<URI, Resource>> set = this.resourceItems;
        for (RegistryItem registryItem : (RegistryItem[]) set.toArray(new RegistryItem[set.size()])) {
            ((Resource) registryItem.getItem()).shutdown();
        }
        this.remoteItems.shutdown();
        this.localItems.shutdown();
        for (RegistryListener registryListener2 : this.registryListeners) {
            registryListener2.afterShutdown();
        }
    }

    @Override
    public synchronized void pause() {
        if (this.registryMaintainer != null) {
            log.fine("Pausing registry maintenance");
            runPendingExecutions(true);
            this.registryMaintainer.stop();
            this.registryMaintainer = null;
        }
    }

    @Override
    public synchronized void resume() {
        if (this.registryMaintainer == null) {
            log.fine("Resuming registry maintenance");
            this.remoteItems.resume();
            RegistryMaintainer createRegistryMaintainer = createRegistryMaintainer();
            this.registryMaintainer = createRegistryMaintainer;
            if (createRegistryMaintainer != null) {
                getConfiguration().getRegistryMaintainerExecutor().execute(this.registryMaintainer);
            }
        }
    }

    @Override
    public synchronized boolean isPaused() {
        return this.registryMaintainer == null;
    }

    
    public synchronized void maintain() {
        if (log.isLoggable(Level.FINEST)) {
            log.finest("Maintaining registry...");
        }
        Iterator<RegistryItem<URI, Resource>> it = this.resourceItems.iterator();
        while (it.hasNext()) {
            RegistryItem<URI, Resource> next = it.next();
            if (next.getExpirationDetails().hasExpired()) {
                if (log.isLoggable(Level.FINER)) {
                    Logger logger = log;
                    logger.finer("Removing expired resource: " + next);
                }
                it.remove();
            }
        }
        for (RegistryItem<URI, Resource> registryItem : this.resourceItems) {
            registryItem.getItem().maintain(this.pendingExecutions, registryItem.getExpirationDetails());
        }
        this.remoteItems.maintain();
        this.localItems.maintain();
        runPendingExecutions(true);
    }

    
    public synchronized void executeAsyncProtocol(Runnable runnable) {
        this.pendingExecutions.add(runnable);
    }

    synchronized void runPendingExecutions(boolean z) {
        if (log.isLoggable(Level.FINEST)) {
            Logger logger = log;
            logger.finest("Executing pending operations: " + this.pendingExecutions.size());
        }
        for (Runnable runnable : this.pendingExecutions) {
            if (z) {
                getConfiguration().getAsyncProtocolExecutor().execute(runnable);
            } else {
                runnable.run();
            }
        }
        if (this.pendingExecutions.size() > 0) {
            this.pendingExecutions.clear();
        }
    }

    public void printDebugLog() {
        if (log.isLoggable(Level.FINE)) {
            log.fine("====================================    REMOTE   ================================================");
            for (RemoteDevice remoteDevice : this.remoteItems.get()) {
                log.fine(remoteDevice.toString());
            }
            log.fine("====================================    LOCAL    ================================================");
            for (LocalDevice localDevice : this.localItems.get()) {
                log.fine(localDevice.toString());
            }
            log.fine("====================================  RESOURCES  ================================================");
            for (RegistryItem<URI, Resource> registryItem : this.resourceItems) {
                log.fine(registryItem.toString());
            }
            log.fine("=================================================================================================");
        }
    }

    @Override
    public void registerPendingRemoteSubscription(RemoteGENASubscription remoteGENASubscription) {
        synchronized (this.pendingSubscriptionsLock) {
            this.pendingSubscriptionsLock.add(remoteGENASubscription);
        }
    }

    @Override
    public void unregisterPendingRemoteSubscription(RemoteGENASubscription remoteGENASubscription) {
        synchronized (this.pendingSubscriptionsLock) {
            if (this.pendingSubscriptionsLock.remove(remoteGENASubscription)) {
                this.pendingSubscriptionsLock.notifyAll();
            }
        }
    }

    @Override
    public RemoteGENASubscription getWaitRemoteSubscription(String str) {
        RemoteGENASubscription remoteSubscription;
        synchronized (this.pendingSubscriptionsLock) {
            remoteSubscription = getRemoteSubscription(str);
            while (remoteSubscription == null && !this.pendingSubscriptionsLock.isEmpty()) {
                try {
                    log.finest("Subscription not found, waiting for pending subscription procedure to terminate.");
                    this.pendingSubscriptionsLock.wait();
                } catch (InterruptedException unused) {
                }
                remoteSubscription = getRemoteSubscription(str);
            }
        }
        return remoteSubscription;
    }
}
