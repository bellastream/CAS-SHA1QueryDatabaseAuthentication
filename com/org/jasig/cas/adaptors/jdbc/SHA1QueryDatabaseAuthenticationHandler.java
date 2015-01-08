package org.jasig.cas.adaptors.jdbc;

import org.jasig.cas.adaptors.jdbc.AbstractJdbcUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.HandlerResult;
import org.jasig.cas.authentication.PreventedException;
import org.jasig.cas.authentication.UsernamePasswordCredential;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.jasig.cas.authentication.principal.DefaultPrincipalFactory;
import org.jasig.cas.authentication.principal.PrincipalFactory;

import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import javax.validation.constraints.NotNull;

import java.security.GeneralSecurityException;
import org.apache.log4j.Logger;

/**
 * A implement of DatabaseAuthenticationHandler with salt 
 * Compatible with the authentication method in django 1.3 
 *
 * @author Bella Stream
 * 2015.1
 */
public class SHA1QueryDatabaseAuthenticationHandler extends AbstractJdbcUsernamePasswordAuthenticationHandler {

    @NotNull
    private String sql;
    /** Factory to create the principal type. **/
    @NotNull
    private PrincipalFactory principalFactory = new DefaultPrincipalFactory();
    private static Logger logger = Logger.getLogger(SHA1QueryDatabaseAuthenticationHandler.class);
    /**
     * {@inheritDoc}
     */
    @Override
    protected final HandlerResult authenticateUsernamePasswordInternal(final UsernamePasswordCredential credential)
            throws GeneralSecurityException, PreventedException {

        final String username = credential.getUsername();
        logger.info(username + " is trying to login.");
        try {
        	final String[] dbPasswordCodes = getJdbcTemplate().queryForObject(this.sql, String.class, username).split("\\$");
        	final String dbSalt = dbPasswordCodes[1];
            final String dbPassword = dbPasswordCodes[2];
            final String encryptedPassword = this.getPasswordEncoder().encode(dbSalt + credential.getPassword());
            if (!dbPassword.equals(encryptedPassword)) {
                 throw new FailedLoginException("Password does not match value on record.");
            }
        } catch (final IncorrectResultSizeDataAccessException e) {
            if (e.getActualSize() == 0) {
                throw new AccountNotFoundException(username + " not found with SQL query");
            } else {
                throw new FailedLoginException("Multiple records found for " + username);
            }
        } catch (final DataAccessException e) {
            throw new PreventedException("SQL exception while executing query for " + username, e);
        } catch (ArrayIndexOutOfBoundsException e){
        	throw new FailedLoginException("Password in database does not in right format.");
        }
        return createHandlerResult(credential, this.principalFactory.createPrincipal(username), null);
    }

    /**
     * @param sql The sql to set.
     */
    public void setSql(final String sql) {
        this.sql = sql;
    }
}