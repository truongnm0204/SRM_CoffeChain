package util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import service.ConfigService;

public class JWTUtils {
    
    
    private static final ConfigService config = ConfigService.getInstance();
    private static final String ISSUER = config.getJwtIssuer();
    
    /**
     * Lấy Algorithm để sign JWT
     * @return Algorithm instance
     */
    private static Algorithm getAlgorithm() {
        String secret = config.getJwtSecret();
        if (StringUtils.isNullOrEmpty(secret)) {
            secret = "BuiVanSon_deptrai_lop_is1905";
        }
        return Algorithm.HMAC256(secret);
    }
    
    /**
     * Tạo JWT token
     * @param userId ID người dùng
     * @param username Tên đăng nhập
     * @param role Vai trò người dùng
     * @return JWT token string
     */
    public static String createToken(String userId, String username, String role) {
        if (StringUtils.isNullOrEmpty(userId) || StringUtils.isNullOrEmpty(username)) {
            throw new IllegalArgumentException("UserId và username không được null hoặc rỗng");
        }
        
        try {
            Algorithm algorithm = getAlgorithm();
            Date now = new Date();
            Date expiration = new Date(now.getTime() + config.getJwtExpiration());
            
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(userId)
                    .withClaim("userId", userId)
                    .withClaim("username", username)
                    .withClaim("role", role != null ? role : "user")
                    .withIssuedAt(now)
                    .withExpiresAt(expiration)
                    .sign(algorithm);
                    
        } catch (JWTCreationException e) {
            throw new RuntimeException("Lỗi khi tạo JWT token: " + e.getMessage(), e);
        }
    }
    
    /**
     * Tạo JWT token với thời gian hết hạn tùy chỉnh
     * @param userId ID người dùng
     * @param username Tên đăng nhập
     * @param role Vai trò người dùng
     * @param expirationMillis Thời gian hết hạn (milliseconds)
     * @return JWT token string
     */
    public static String createTokenWithExpiration(String userId, String username, String role, long expirationMillis) {
        if (StringUtils.isNullOrEmpty(userId) || StringUtils.isNullOrEmpty(username)) {
            throw new IllegalArgumentException("UserId và username không được null hoặc rỗng");
        }
        
        try {
            Algorithm algorithm = getAlgorithm();
            Date now = new Date();
            Date expiration = new Date(now.getTime() + expirationMillis);
            
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(userId)
                    .withClaim("userId", userId)
                    .withClaim("username", username)
                    .withClaim("role", role != null ? role : "user")
                    .withIssuedAt(now)
                    .withExpiresAt(expiration)
                    .sign(algorithm);
                    
        } catch (JWTCreationException e) {
            throw new RuntimeException("Lỗi khi tạo JWT token: " + e.getMessage(), e);
        }
    }
    
    /**
     * Tạo JWT token với các claims tùy chỉnh
     * @param userId ID người dùng
     * @param username Tên đăng nhập
     * @param role Vai trò người dùng
     * @param customClaims Map chứa các claims tùy chỉnh
     * @return JWT token string
     */
    public static String createTokenWithClaims(String userId, String username, String role, Map<String, Object> customClaims) {
        if (StringUtils.isNullOrEmpty(userId) || StringUtils.isNullOrEmpty(username)) {
            throw new IllegalArgumentException("UserId và username không được null hoặc rỗng");
        }
        
        try {
            Algorithm algorithm = getAlgorithm();
            Date now = new Date();
            Date expiration = new Date(now.getTime() + config.getJwtExpiration());
            
            var builder = JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(userId)
                    .withClaim("userId", userId)
                    .withClaim("username", username)
                    .withClaim("role", role != null ? role : "user")
                    .withIssuedAt(now)
                    .withExpiresAt(expiration);
            
            // Thêm custom claims
            if (customClaims != null) {
                for (Map.Entry<String, Object> entry : customClaims.entrySet()) {
                    Object value = entry.getValue();
                    if (value instanceof String) {
                        builder.withClaim(entry.getKey(), (String) value);
                    } else if (value instanceof Integer) {
                        builder.withClaim(entry.getKey(), (Integer) value);
                    } else if (value instanceof Long) {
                        builder.withClaim(entry.getKey(), (Long) value);
                    } else if (value instanceof Boolean) {
                        builder.withClaim(entry.getKey(), (Boolean) value);
                    } else if (value instanceof Date) {
                        builder.withClaim(entry.getKey(), (Date) value);
                    }
                    // Có thể thêm các kiểu dữ liệu khác nếu cần
                }
            }
            
            return builder.sign(algorithm);
                    
        } catch (JWTCreationException e) {
            throw new RuntimeException("Lỗi khi tạo JWT token: " + e.getMessage(), e);
        }
    }
    
    /**
     * Xác thực JWT token
     * @param token JWT token cần xác thực
     * @return true nếu token hợp lệ
     */
    public static boolean validateToken(String token) {
        try {
            if (StringUtils.isNullOrEmpty(token)) {
                return false;
            }
            
            Algorithm algorithm = getAlgorithm();
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();
            
            verifier.verify(token);
            return true;
            
        } catch (JWTVerificationException e) {
            return false;
        }
    }
    
    /**
     * Decode JWT token (không verify)
     * @param token JWT token
     * @return DecodedJWT object hoặc null nếu lỗi
     */
    public static DecodedJWT decodeToken(String token) {
        try {
            if (StringUtils.isNullOrEmpty(token)) {
                return null;
            }
            return JWT.decode(token);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Verify và decode JWT token
     * @param token JWT token
     * @return DecodedJWT object hoặc null nếu token không hợp lệ
     */
    public static DecodedJWT verifyAndDecodeToken(String token) {
        try {
            if (StringUtils.isNullOrEmpty(token)) {
                return null;
            }
            
            Algorithm algorithm = getAlgorithm();
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();
            
            return verifier.verify(token);
            
        } catch (JWTVerificationException e) {
            return null;
        }
    }
    
    /**
     * Lấy user ID từ JWT token
     * @param token JWT token
     * @return User ID hoặc null nếu không tìm thấy
     */
    public static String getUserId(String token) {
        try {
            DecodedJWT decodedJWT = decodeToken(token);
            if (decodedJWT != null) {
                return decodedJWT.getClaim("userId").asString();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Lấy username từ JWT token
     * @param token JWT token
     * @return Username hoặc null nếu không tìm thấy
     */
    public static String getUsername(String token) {
        try {
            DecodedJWT decodedJWT = decodeToken(token);
            if (decodedJWT != null) {
                return decodedJWT.getClaim("username").asString();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Lấy role từ JWT token
     * @param token JWT token
     * @return Role hoặc null nếu không tìm thấy
     */
    public static String getRole(String token) {
        try {
            DecodedJWT decodedJWT = decodeToken(token);
            if (decodedJWT != null) {
                return decodedJWT.getClaim("role").asString();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Lấy subject từ JWT token
     * @param token JWT token
     * @return Subject hoặc null nếu không tìm thấy
     */
    public static String getSubject(String token) {
        try {
            DecodedJWT decodedJWT = decodeToken(token);
            if (decodedJWT != null) {
                return decodedJWT.getSubject();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Lấy custom claim từ JWT token
     * @param token JWT token
     * @param claimName Tên claim
     * @return Giá trị claim hoặc null nếu không tìm thấy
     */
    public static String getCustomClaim(String token, String claimName) {
        try {
            DecodedJWT decodedJWT = decodeToken(token);
            if (decodedJWT != null) {
                return decodedJWT.getClaim(claimName).asString();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Lấy tất cả claims từ JWT token
     * @param token JWT token
     * @return Map chứa tất cả claims
     */
    public static Map<String, Object> getAllClaims(String token) {
        Map<String, Object> claims = new HashMap<>();
        try {
            DecodedJWT decodedJWT = decodeToken(token);
            if (decodedJWT != null) {
                decodedJWT.getClaims().forEach((key, claim) -> {
                    if (claim.asString() != null) {
                        claims.put(key, claim.asString());
                    } else if (claim.asInt() != null) {
                        claims.put(key, claim.asInt());
                    } else if (claim.asLong() != null) {
                        claims.put(key, claim.asLong());
                    } else if (claim.asBoolean() != null) {
                        claims.put(key, claim.asBoolean());
                    } else if (claim.asDate() != null) {
                        claims.put(key, claim.asDate());
                    }
                });
            }
            return claims;
        } catch (Exception e) {
            return claims;
        }
    }
    
    /**
     * Kiểm tra token có hết hạn không
     * @param token JWT token
     * @return true nếu token đã hết hạn
     */
    public static boolean isTokenExpired(String token) {
        try {
            DecodedJWT decodedJWT = decodeToken(token);
            if (decodedJWT != null) {
                Date expiration = decodedJWT.getExpiresAt();
                return expiration != null && expiration.before(new Date());
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }
    
    /**
     * Lấy thời gian hết hạn từ token
     * @param token JWT token
     * @return Date object hoặc null nếu không có
     */
    public static Date getExpirationDate(String token) {
        try {
            DecodedJWT decodedJWT = decodeToken(token);
            if (decodedJWT != null) {
                return decodedJWT.getExpiresAt();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Lấy thời gian tạo token
     * @param token JWT token
     * @return Date object hoặc null nếu không có
     */
    public static Date getIssuedAt(String token) {
        try {
            DecodedJWT decodedJWT = decodeToken(token);
            if (decodedJWT != null) {
                return decodedJWT.getIssuedAt();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Refresh JWT token (tạo token mới với thời gian hết hạn mới)
     * @param token Token cũ
     * @return Token mới hoặc null nếu token cũ không hợp lệ
     */
    public static String refreshToken(String token) {
        try {
            // Chỉ decode mà không verify để lấy thông tin (cho phép token hết hạn)
            DecodedJWT decodedJWT = decodeToken(token);
            if (decodedJWT != null) {
                String userId = decodedJWT.getClaim("userId").asString();
                String username = decodedJWT.getClaim("username").asString();
                String role = decodedJWT.getClaim("role").asString();
                
                if (StringUtils.isNotNullAndNotEmpty(userId) && StringUtils.isNotNullAndNotEmpty(username)) {
                    return createToken(userId, username, role);
                }
            }
            return null;
            
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Kiểm tra user có quyền admin không
     * @param token JWT token
     * @return true nếu user là admin
     */
    public static boolean isAdmin(String token) {
        String role = getRole(token);
        return "admin".equalsIgnoreCase(role);
    }
    
    /**
     * Kiểm tra user có một trong các roles cho phép không
     * @param token JWT token
     * @param allowedRoles Mảng các roles được phép
     * @return true nếu user có role trong danh sách cho phép
     */
    public static boolean hasRole(String token, String[] allowedRoles) {
        if (allowedRoles == null || allowedRoles.length == 0) {
            return true;
        }
        
        String userRole = getRole(token);
        return StringUtils.containsIgnoreCase(userRole, allowedRoles);
    }
    
    /**
     * Kiểm tra token có hợp lệ và chưa hết hạn không
     * @param token JWT token
     * @return true nếu token hợp lệ và chưa hết hạn
     */
    public static boolean isValidAndNotExpired(String token) {
        return validateToken(token) && !isTokenExpired(token);
    }
    
    /**
     * Lấy thời gian còn lại của token (seconds)
     * @param token JWT token
     * @return Số giây còn lại hoặc 0 nếu đã hết hạn
     */
    public static long getTimeToExpire(String token) {
        try {
            Date expiration = getExpirationDate(token);
            if (expiration != null) {
                long timeToExpire = (expiration.getTime() - System.currentTimeMillis()) / 1000;
                return Math.max(0, timeToExpire);
            }
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }
}