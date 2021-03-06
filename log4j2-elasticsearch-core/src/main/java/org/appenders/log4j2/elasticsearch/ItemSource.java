package org.appenders.log4j2.elasticsearch;

/*-
 * #%L
 * log4j2-elasticsearch
 * %%
 * Copyright (C) 2018 Rafal Foltynski
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.Serializable;

/**
 * Batch item wrapper. Allows to add complex objects to {@link BatchDelivery}
 *
 * @param <T> underlying batch item type
 */
public interface ItemSource<T> extends Serializable {

    /**
     * @return wrapped batch item
     */
    T getSource();

    /**
     * Lifecycle
     *
     * MUST be invoked after batch is completed. Allows to clean up underlying item.
     */
    default void release() {
        // noop
    }

}
