/*
 * Copyright 2019 Nick Rammos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.nickrm.jflux.api.response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.nickrm.jflux.domain.Measurement;

/**
 * An InfluxQL result, acts as a wrapper for the (possibly multiple) series returned.
 * <p>
 * A result will contain more than one series if more than one measurement were queried. For
 * instance, the following query will return a result with two series:
 * <p><blockquote><pre>{@code
 * SELECT * FROM measurement_1, measurement_2
 * }</pre></blockquote>
 *
 * @see Builder
 * @since 1.0.0
 */
public final class QueryResult {

    private final int statementId;
    private final String error;
    private final List<Measurement> results;

    /**
     * Instances of this class can only be constructed using {@link Builder}.
     *
     * @param builder used to construct the instance
     */
    private QueryResult(Builder builder) {
        statementId = builder.statementId;
        error = builder.error;
        results = builder.results;
    }

    /**
     * Gets the ID of the statement that this result corresponds to.
     * <p>
     * The ID is a zero-based index used to differentiate the multiple results for multi-statement
     * queries. For single statement queries this will just be zero and can be ignored.
     *
     * @return this result's statement ID
     */
    public int getStatementId() {
        return statementId;
    }

    /**
     * Gets the error message for this result if available.
     *
     * @return the error message, or {@code null} if none
     */
    public String getError() {
        return error;
    }

    /**
     * Gets the series contained in this result.
     *
     * @return this result's series, or an empty list if none are available
     */
    public List<Measurement> getResults() {
        return new ArrayList<>(results);
    }

    @Override
    public String toString() {
        return "QueryResult{" + "statementId=" + statementId + ", error='" + error + '\''
                + ", results=" + results + '}';
    }

    /**
     * Creates instances of {@link QueryResult}.
     */
    public static final class Builder {

        private int statementId;
        private String error;
        private List<Measurement> results = Collections.emptyList();

        /**
         * Sets the statement ID for the query result to be constructed.
         *
         * @param statementId the statement ID
         *
         * @return this builder
         */
        public Builder statementId(int statementId) {
            this.statementId = statementId;
            return this;
        }

        /**
         * Sets the error message of the query result to be constructed.
         *
         * @param error the error message for the result, {@code null} means no error
         *
         * @return this builder
         */
        public Builder error(String error) {
            this.error = error;
            return this;
        }

        /**
         * Sets the results of the query.
         *
         * @param results the query results
         *
         * @return this builder
         */
        public Builder series(List<Measurement> results) {
            this.results = results;
            return this;
        }

        /**
         * Builds a new instance of {@link QueryResult} using the values set in this builder.
         *
         * @return the constructed {@link QueryResult}
         */
        public QueryResult build() {
            return new QueryResult(this);
        }
    }
}
