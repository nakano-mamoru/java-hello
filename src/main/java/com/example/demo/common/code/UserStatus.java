package com.example.demo.common.code;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * USER_STATUS
 * <p>
 * 自動生成されたEnumクラス
 * 物理名: USER_STATUS
 */
public enum UserStatus {

    /** 有効 */
    ACTIVE("A"),
    /** 無効 */
    INACTIVE("I"),
    /** 削除 */
    DELETED("D");

    /** コード値 */
    private final String code;

    UserStatus(String code) {
        this.code = code;
    }

    /**
     * コード値を取得します。
     *
     * @return コード
     */
    @JsonValue
    public String getCode() {
        return code;
    }

    /**
     * コード値からEnumを取得します。
     *
     * @param code コード値
     * @return Enum
     * @throws IllegalArgumentException 該当コードが存在しない場合
     */
    @JsonCreator
    public static UserStatus fromCode(String code) {

        if (code == null) {
            return null;
        }

        for (UserStatus v : values()) {
            if (v.code.equals(code)) {
                return v;
            }
        }

        throw new IllegalArgumentException("Invalid code: " + code);
    }

    /**
     * 指定されたコードがこのEnumに存在するか判定します。
     *
     * @param code コード値
     * @return 存在する場合 true
     */
    public static boolean hasCode(String code) {

        if (code == null) {
            return false;
        }

        for (UserStatus v : values()) {
            if (v.code.equals(code)) {
                return true;
            }
        }

        return false;
    }
}
