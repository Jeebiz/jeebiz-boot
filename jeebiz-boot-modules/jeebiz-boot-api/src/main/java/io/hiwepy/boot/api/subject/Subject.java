package io.hiwepy.boot.api.subject;

import java.util.Collection;
import java.util.List;

public interface Subject {

    Object getPrincipal();

    boolean isPermitted(String permission);
    
    boolean[] isPermitted(String... permissions);

    boolean isPermittedAll(String... permissions);
	
    /**
     * Returns {@code true} if this Subject has the specified role, {@code false} otherwise.
     *
     * @param roleIdentifier the application-specific role identifier (usually a role id or role name).
     * @return {@code true} if this Subject has the specified role, {@code false} otherwise.
     */
    boolean hasRole(String roleIdentifier);

    /**
     * Checks if this Subject has the specified roles, returning a boolean array indicating
     * which roles are associated.
     * <p/>
     * This is primarily a performance-enhancing method to help reduce the number of
     * {@link #hasRole} invocations over the wire in client/server systems.
     *
     * @param roleIdentifiers the application-specific role identifiers to check (usually role ids or role names).
     * @return a boolean array where indices correspond to the index of the
     *         roles in the given identifiers.  A true value indicates this Subject has the
     *         role at that index.  False indicates this Subject does not have the role at that index.
     */
    boolean[] hasRoles(List<String> roleIdentifiers);

    /**
     * Returns {@code true} if this Subject has all of the specified roles, {@code false} otherwise.
     *
     * @param roleIdentifiers the application-specific role identifiers to check (usually role ids or role names).
     * @return true if this Subject has all the roles, false otherwise.
     */
    boolean hasAllRoles(Collection<String> roleIdentifiers);

    /**
     * Returns {@code true} if this Subject/user proved their identity <em>during their current session</em>
     * by providing valid credentials matching those known to the system, {@code false} otherwise.
     * <p/>
     * Note that even if this Subject's identity has been remembered via 'remember me' services, this method will
     * still return {@code false} unless the user has actually logged in with proper credentials <em>during their
     * current session</em>.  See the {@link #isRemembered() isRemembered()} method JavaDoc for more.
     *
     * @return {@code true} if this Subject proved their identity during their current session
     *         by providing valid credentials matching those known to the system, {@code false} otherwise.
     * @since 0.9
     */
    boolean isAuthenticated();

    boolean isRemembered();
    
}
