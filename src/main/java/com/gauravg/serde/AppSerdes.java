/*
 * Copyright (c) 2019. Prashant Kumar Pandey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.gauravg.serde;

import com.gauravg.types.A;
import com.gauravg.types.C;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory class for Serdes
 *
 * @author prashant
 * @author www.learningjournal.guru
 */

public class AppSerdes extends Serdes {


    static final class ASerde extends WrapperSerde<A> {
        ASerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>());
        }
    }

    public static Serde<A> A() {
        ASerde serde = new ASerde();

        Map<String, Object> serdeConfigs = new HashMap<>();
        serdeConfigs.put(JsonDeserializer.VALUE_CLASS_NAME_CONFIG, A.class);
        serde.configure(serdeConfigs, false);

        return serde;
    }

    static final class CSerde extends WrapperSerde<C> {
        CSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>());
        }
    }

    public static Serde<C> C() {
        CSerde serde = new CSerde();

        Map<String, Object> serdeConfigs = new HashMap<>();
        serdeConfigs.put(JsonDeserializer.VALUE_CLASS_NAME_CONFIG, C.class);
        serde.configure(serdeConfigs, false);

        return serde;
    }

}
