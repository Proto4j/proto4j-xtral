package org.proto4j.xtral.config;//@date 11.09.2022

/**
 * Configuration factories are used to provide an instance of the
 * {@link XTralConfiguration}.
 * <p>
 * This factory is designed to throw an exception on failure which will
 * stop the whole generation process.
 *
 * @param <T> the server or client main class
 * @since 1.0
 */
public interface XTralConfigurationFactory<T> {

    /**
     * Constructs a new {@code XTralConfiguration}.
     *
     * @return constructed configuration, or {@code null} if an error occurred
     */
    public abstract XTralConfiguration<T> createConfiguration();
}
