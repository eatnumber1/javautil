/*
 * Copyright 2007 Russell Harmon
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.eatnumber1.util.numbers;

/**
 * @author Russell Harmon
 * @since Jul 14, 2009
 */
public interface MutableNumber extends Number {
    void byteValue( byte value );

    void doubleValue( double value );

    void floatValue( float value );

    void intValue( int value );

    void longValue( long value );

    void shortValue( short value );
}
