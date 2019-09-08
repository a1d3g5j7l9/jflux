package com.nickrammos.jflux;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.nickrammos.jflux.api.JFluxHttpClient;
import com.nickrammos.jflux.api.response.ResponseMetadata;
import com.nickrammos.jflux.domain.Measurement;
import com.nickrammos.jflux.domain.Point;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client for InfluxDB.
 * <p>
 * Provides convenient abstractions over {@link JFluxHttpClient}.
 *
 * @see JFluxHttpClient
 */
public final class JFluxClient implements AutoCloseable {

    static final String INTERNAL_DATABASE_NAME = "_internal";

    private static final Logger LOGGER = LoggerFactory.getLogger(JFluxClient.class);

    private final JFluxHttpClient httpClient;

    /**
     * Initializes a new instance, setting the HTTP client used to query the InfluxDB API.
     *
     * @param httpClient the InfluxDB HTTP API client
     *
     * @throws IOException if the InfluxDB instance is unreachable
     * @see Builder
     */
    JFluxClient(JFluxHttpClient httpClient) throws IOException {
        this.httpClient = httpClient;

        try {
            ResponseMetadata responseMetadata = httpClient.ping();
            LOGGER.info("Connected to InfluxDB {} {} instance at {}",
                    responseMetadata.getDbBuildType(), responseMetadata.getDbVersion(),
                    httpClient.getHostUrl());
        } catch (IOException e) {
            throw new IOException("Could not connect to InfluxDB instance", e);
        }
    }

    /**
     * Gets the existing databases.
     *
     * @return the existing databases, should always contain at least the internal one
     */
    public List<String> getDatabases() {
        Measurement queryResult = callApi(() -> httpClient.query("SHOW DATABASES"));
        List<String> databases = new ArrayList<>();
        for (Point point : queryResult.getPoints()) {
            databases.addAll(point.getTags().values());
        }
        LOGGER.debug("Found databases: {}", databases);
        return databases;
    }

    /**
     * Gets a value indicating whether the specified database exists.
     *
     * @param databaseName the database name to check, not {@code null}
     *
     * @return {@code true} if the database exists, {@code false} otherwise
     *
     * @throws NullPointerException if {@code databaseName} is {@code null}
     */
    public boolean databaseExists(String databaseName) {
        if (databaseName == null) {
            throw new NullPointerException("Database name cannot be null");
        }
        return getDatabases().contains(databaseName);
    }

    /**
     * Creates a new database with the specified name.
     *
     * @param databaseName the database to create, not {@code null}
     *
     * @throws NullPointerException     if {@code databaseName} is {@code null}
     * @throws IllegalArgumentException if the database already exists.
     */
    public void createDatabase(String databaseName) {
        if (databaseName == null) {
            throw new NullPointerException("Database name cannot be null");
        }

        if (databaseExists(databaseName)) {
            throw new IllegalArgumentException("Database " + databaseName + " already exists");
        }

        callApi(() -> httpClient.execute("CREATE DATABASE \"" + databaseName + "\""));
        LOGGER.info("Created database '{}'", databaseName);
    }

    /**
     * Drops the specified database.
     *
     * @param databaseName the database to drop, not {@code null}
     *
     * @throws NullPointerException     if {@code databaseName} is {@code null}
     * @throws IllegalArgumentException if the database does not exist
     * @throws IllegalArgumentException if trying to drop the internal InfluxDB database
     */
    public void dropDatabase(String databaseName) {
        if (databaseName == null) {
            throw new NullPointerException("Database cannot be null");
        }

        if (INTERNAL_DATABASE_NAME.equals(databaseName)) {
            throw new IllegalArgumentException("Cannot drop internal database");
        }

        if (!databaseExists(databaseName)) {
            throw new IllegalArgumentException("Unknown database " + databaseName);
        }

        callApi(() -> httpClient.execute("DROP DATABASE \"" + databaseName + "\""));
        LOGGER.info("Dropped database '{}'", databaseName);
    }

    private void callApi(IOThrowingRunnable apiMethod) {
        try {
            apiMethod.run();
        } catch (IOException e) {
            throw new IllegalStateException("Connection to InfluxDB lost", e);
        }
    }

    private <T> T callApi(IOThrowingSupplier<T> apiMethod) {
        try {
            return apiMethod.get();
        } catch (IOException e) {
            throw new IllegalStateException("Connection to InfluxDB lost", e);
        }
    }

    @Override
    public void close() throws Exception {
        httpClient.close();
    }

    /**
     * Used to construct instances of {@link JFluxClient}.
     */
    public static final class Builder {

        private String host;

        /**
         * Initializes a new builder instance, setting the InfluxDB host URL.
         *
         * @param host the InfluxDB host URL, e.g. {@code http://localhost:8086}
         */
        public Builder(String host) {
            this.host = host;
        }

        /**
         * Constructs a new {@link JFluxClient} instance from this builder's configuration.
         *
         * @return the new client instance
         *
         * @throws IOException if the InfluxDB instance is unreachable
         */
        public JFluxClient build() throws IOException {
            JFluxHttpClient httpClient = new JFluxHttpClient.Builder(host).build();
            return new JFluxClient(httpClient);
        }
    }

    /**
     * Convenience interface for runnables that throw IOExceptions.
     */
    private interface IOThrowingRunnable {

        void run() throws IOException;
    }

    /**
     * Convenience interface for suppliers that throw IOExceptions.
     *
     * @param <T> type of the return value
     */
    private interface IOThrowingSupplier<T> {

        T get() throws IOException;
    }
}