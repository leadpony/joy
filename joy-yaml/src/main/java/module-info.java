/*
 * Copyright 2019-2020 the Joy Authors.
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
/**
 * Defines the provider of Jakarta JSON Processing API (JSON-P).
 */
module org.leadpony.joy.yaml {
    requires org.leadpony.joy.core;
    requires org.snakeyaml.engine.v2;
    requires jakarta.json;

    provides jakarta.json.spi.JsonProvider
        with org.leadpony.joy.yaml.YamlProvider;
}
