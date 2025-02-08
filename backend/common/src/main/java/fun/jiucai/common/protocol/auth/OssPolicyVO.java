package fun.jiucai.common.protocol.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author godotg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OssPolicyVO {

    /**
     * oss生成的协议
     */
    private String policy;
    /**
     * 访问码accessKey
     */
    private String accessKeyId;
    /**
     * 签名
     */
    private String signature;
    /**
     * 文件夹，路径
     */
    private String dir;
    /**
     * 地址
     */
    private String host;
    /**
     * 过期时间
     */
    private String expire;

}
