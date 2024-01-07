package org.fourthline.cling.model;

import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.StateVariable;
import org.fourthline.cling.model.state.StateVariableAccessor;
import org.fourthline.cling.model.state.StateVariableValue;
import org.seamless.util.Exceptions;
import org.seamless.util.Reflections;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DefaultServiceManager<T> implements ServiceManager<T> {
    private static Logger log = Logger.getLogger(DefaultServiceManager.class.getName());
    protected final ReentrantLock lock;
    protected PropertyChangeSupport propertyChangeSupport;
    protected final LocalService<T> service;
    protected final Class<T> serviceClass;
    protected T serviceImpl;

    protected int getLockTimeoutMillis() {
        return 500;
    }

    protected Collection<StateVariableValue> readInitialEventedStateVariableValues() throws Exception {
        return null;
    }

    protected DefaultServiceManager(LocalService<T> localService) {
        this(localService, null);
    }

    public DefaultServiceManager(LocalService<T> localService, Class<T> cls) {
        this.lock = new ReentrantLock(true);
        this.service = localService;
        this.serviceClass = cls;
    }

    
    public void lock() {
        try {
            if (this.lock.tryLock(getLockTimeoutMillis(), TimeUnit.MILLISECONDS)) {
                if (log.isLoggable(Level.FINEST)) {
                    log.finest("Acquired lock");
                    return;
                }
                return;
            }
            throw new RuntimeException("Failed to acquire lock in milliseconds: " + getLockTimeoutMillis());
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to acquire lock:" + e);
        }
    }

    
    public void unlock() {
        if (log.isLoggable(Level.FINEST)) {
            log.finest("Releasing lock");
        }
        this.lock.unlock();
    }

    @Override
    public LocalService<T> getService() {
        return this.service;
    }

    @Override
    public T getImplementation() {
        lock();
        try {
            if (this.serviceImpl == null) {
                init();
            }
            return this.serviceImpl;
        } finally {
            unlock();
        }
    }

    @Override
    public PropertyChangeSupport getPropertyChangeSupport() {
        lock();
        try {
            if (this.propertyChangeSupport == null) {
                init();
            }
            return this.propertyChangeSupport;
        } finally {
            unlock();
        }
    }

    @Override
    public void execute(Command<T> command) throws Exception {
        lock();
        try {
            command.execute(this);
        } finally {
            unlock();
        }
    }

    @Override
    public Collection<StateVariableValue> getCurrentState() throws Exception {
        StateVariable<LocalService>[] stateVariables;
        lock();
        try {
            Collection<StateVariableValue> readInitialEventedStateVariableValues = readInitialEventedStateVariableValues();
            if (readInitialEventedStateVariableValues != null) {
                log.fine("Obtained initial state variable values for event, skipping individual state variable accessors");
                return readInitialEventedStateVariableValues;
            }
            ArrayList arrayList = new ArrayList();
            for (StateVariable<LocalService> stateVariable : getService().getStateVariables()) {
                if (stateVariable.getEventDetails().isSendEvents()) {
                    StateVariableAccessor accessor = getService().getAccessor(stateVariable);
                    if (accessor == null) {
                        throw new IllegalStateException("No accessor for evented state variable");
                    }
                    arrayList.add(accessor.read(stateVariable, getImplementation()));
                }
            }
            return arrayList;
        } finally {
            unlock();
        }
    }

    protected Collection<StateVariableValue> getCurrentState(String[] strArr) throws Exception {
        lock();
        try {
            ArrayList arrayList = new ArrayList();
            for (String str : strArr) {
                String trim = str.trim();
                StateVariable<LocalService> stateVariable = getService().getStateVariable(trim);
                if (stateVariable != null && stateVariable.getEventDetails().isSendEvents()) {
                    StateVariableAccessor accessor = getService().getAccessor(stateVariable);
                    if (accessor == null) {
                        log.warning("Ignoring evented state variable without accessor: " + trim);
                    } else {
                        arrayList.add(accessor.read(stateVariable, getImplementation()));
                    }
                }
                log.fine("Ignoring unknown or non-evented state variable: " + trim);
            }
            return arrayList;
        } finally {
            unlock();
        }
    }

    protected void init() {
        log.fine("No service implementation instance available, initializing...");
        try {
            T createServiceInstance = createServiceInstance();
            this.serviceImpl = createServiceInstance;
            PropertyChangeSupport createPropertyChangeSupport = createPropertyChangeSupport(createServiceInstance);
            this.propertyChangeSupport = createPropertyChangeSupport;
            createPropertyChangeSupport.addPropertyChangeListener(createPropertyChangeListener(this.serviceImpl));
        } catch (Exception e) {
            throw new RuntimeException("Could not initialize implementation: " + e, e);
        }
    }

    protected T createServiceInstance() throws Exception {
        Class<T> cls = this.serviceClass;
        if (cls == null) {
            throw new IllegalStateException("Subclass has to provide service class or override createServiceInstance()");
        }
        try {
            return cls.getConstructor(LocalService.class).newInstance(getService());
        } catch (NoSuchMethodException unused) {
            Logger logger = log;
            logger.fine("Creating new service implementation instance with no-arg constructor: " + this.serviceClass.getName());
            return this.serviceClass.newInstance();
        }
    }

    protected PropertyChangeSupport createPropertyChangeSupport(T t) throws Exception {
        Method getterMethod = Reflections.getGetterMethod(t.getClass(), "propertyChangeSupport");
        if (getterMethod != null && PropertyChangeSupport.class.isAssignableFrom(getterMethod.getReturnType())) {
            Logger logger = log;
            logger.fine("Service implementation instance offers PropertyChangeSupport, using that: " + t.getClass().getName());
            return (PropertyChangeSupport) getterMethod.invoke(t, new Object[0]);
        }
        Logger logger2 = log;
        logger2.fine("Creating new PropertyChangeSupport for service implementation: " + t.getClass().getName());
        return new PropertyChangeSupport(t);
    }

    protected PropertyChangeListener createPropertyChangeListener(T t) throws Exception {
        return new DefaultPropertyChangeListener();
    }

    public String toString() {
        return "(" + getClass().getSimpleName() + ") Implementation: " + this.serviceImpl;
    }

    

    public class DefaultPropertyChangeListener implements PropertyChangeListener {
        protected DefaultPropertyChangeListener() {
        }

        @Override
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            Logger logger = DefaultServiceManager.log;
            logger.finer("Property change event on local service: " + propertyChangeEvent.getPropertyName());
            if (propertyChangeEvent.getPropertyName().equals(ServiceManager.EVENTED_STATE_VARIABLES)) {
                return;
            }
            String[] fromCommaSeparatedList = ModelUtil.fromCommaSeparatedList(propertyChangeEvent.getPropertyName());
            Logger logger2 = DefaultServiceManager.log;
            logger2.fine("Changed variable names: " + Arrays.toString(fromCommaSeparatedList));
            try {
                Collection<StateVariableValue> currentState = DefaultServiceManager.this.getCurrentState(fromCommaSeparatedList);
                if (currentState.isEmpty()) {
                    return;
                }
                DefaultServiceManager.this.getPropertyChangeSupport().firePropertyChange(ServiceManager.EVENTED_STATE_VARIABLES, (Object) null, currentState);
            } catch (Exception e) {
                Logger logger3 = DefaultServiceManager.log;
                Level level = Level.SEVERE;
                logger3.log(level, "Error reading state of service after state variable update event: " + Exceptions.unwrap(e), (Throwable) e);
            }
        }
    }
}
