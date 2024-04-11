package com.jobosint.collaboration.transform;

public interface BeanTransformer<S, T> {
    T transform(S input);
}
