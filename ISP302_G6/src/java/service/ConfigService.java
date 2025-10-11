package service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration service để đọc các properties từ file config.properties
 */
public class ConfigService {
    private static ConfigService instance;
    private Properties properties;
    
    private ConfigService() {
        loadProperties();
    }
    
    public static ConfigService getInstance() {
        if (instance == null) {
            instance = new ConfigService();
        }
        return instance;
    }
    
    private void loadProperties() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("Không thể tìm thấy file config.properties");
                return;
            }
            properties.load(input);
        } catch (IOException e) {
            System.err.println("Lỗi khi đọc file config.properties: " + e.getMessage());
        }
    }
    
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    // Supabase configuration getters
    public String getSupabaseUrl() {
        return getProperty("supabase.url");
    }
    
    public String getSupabaseAnonKey() {
        return getProperty("supabase.anon.key");
    }
    
    public String getSupabaseServiceRoleKey() {
        return getProperty("supabase.service.role.key");
    }
    
    // Database configuration getters
    public String getDatabaseUrl() {
        return getProperty("db.url");
    }
    
    public String getDatabaseUsername() {
        return getProperty("db.username");
    }
    
    public String getDatabasePassword() {
        return getProperty("db.password");
    }
    
    public String getDatabaseDriver() {
        return getProperty("db.driver");
    }
    
    // JWT configuration getters
    public String getJwtSecret() {
        return getProperty("jwt.secret");
    }
    public String getJwtIssuer() {
        return getProperty("jwt.issuer");
    }
    public long getJwtExpiration() {
        String expiration = getProperty("jwt.expiration", "86400000");
        return Long.parseLong(expiration);
    }
    
    // Application configuration getters
    public String getAppName() {
        return getProperty("app.name");
    }
    
    public String getAppVersion() {
        return getProperty("app.version");
    }
    
    public String getAppEnvironment() {
        return getProperty("app.environment");
    }
    
    // File upload configuration getters
    public long getUploadMaxSize() {
        String maxSize = getProperty("upload.max.size", "10485760");
        return Long.parseLong(maxSize);
    }
    
    public String[] getAllowedFileTypes() {
        String types = getProperty("upload.allowed.types", "");
        return types.split(",");
    }
    
    // Email configuration getters
    public String getEmailSmtpHost() {
        return getProperty("email.smtp.host");
    }
    
    public int getEmailSmtpPort() {
        String port = getProperty("email.smtp.port", "587");
        return Integer.parseInt(port);
    }
    
    public String getEmailUsername() {
        return getProperty("email.username");
    }
    
    public String getEmailPassword() {
        return getProperty("email.password");
    }
    
    public boolean isEmailAuthEnabled() {
        String auth = getProperty("email.auth", "true");
        return Boolean.parseBoolean(auth);
    }
    
    public boolean isEmailStartTlsEnabled() {
        String starttls = getProperty("email.starttls", "true");
        return Boolean.parseBoolean(starttls);
    }

    // Google OAuth getters
    public String getGoogleClientId() {
        return getProperty("google.client.id");
    }

    public String getGoogleClientSecret() {
        return getProperty("google.client.secret");
    }

    public String getGoogleGrantType() {
        return getProperty("google.client.granttype", "authorization_code");
    }

    public String getGoogleRedirectUri() {
        return getProperty("google.redirect.uri");
    }

    public String getGoogleTokenEndpoint() {
        return getProperty("google.redirect.gettoken", "https://oauth2.googleapis.com/token");
    }

    public String getGoogleUserInfoEndpoint() {
        return getProperty("google.redirect.getinfo", "https://www.googleapis.com/oauth2/v1/userinfo?access_token=");
    }
}