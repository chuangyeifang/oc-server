package com.oc.domain.offline;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * @author chuangyeifang
 */
@Getter
@Setter
@ToString
public class OfflinePacket {

    private Long id;

    private String addrTo;

    private String addrFrom;

    private String type;

    private Date createTime;

    private String packet;
}