package com.flicksoft.util;

import java.lang.annotation.*;
@Documented //we want the annotation to show up in the Javadocs 
@Retention(RetentionPolicy.RUNTIME) //we want annotation metadata to be exposed at runtime

public @interface Key {

}
