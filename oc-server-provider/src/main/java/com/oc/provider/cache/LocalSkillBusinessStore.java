package com.oc.provider.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.oc.domain.skill.SkillBusiness;
import com.oc.provider.context.SpringContext;
import com.oc.service.skill.SkillBusinessService;
import com.oc.service.skill.impl.SkillBusinessServiceImpl;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author chuangyeifang
 */
public class LocalSkillBusinessStore {

    private SkillBusinessService skillBusinessService = SpringContext.getBean(SkillBusinessServiceImpl.class);

    private static LoadingCache<Condition, SkillBusiness> skillBusinessLoadingCache;

    private LocalSkillBusinessStore () {
        initCache();
    }

    private void initCache() {
        skillBusinessLoadingCache = CacheBuilder.newBuilder()
                .refreshAfterWrite(5, TimeUnit.MINUTES)
                .build(new CacheLoader<Condition, SkillBusiness>() {
                    @Override
                    public SkillBusiness load(Condition condition) throws Exception {
                        return skillBusinessService.obtainSkillBusiness(condition.tenantCode, condition.businessCode);
                    }
                });
    }

    public SkillBusiness get(String tenantCode, String businessCode) {
        Condition condition = new Condition(tenantCode, businessCode);
        try {
            return skillBusinessLoadingCache.get(condition);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static LocalSkillBusinessStore getInst() {
        return Single.instance;
    }

    private static class Single {
        private static LocalSkillBusinessStore instance = new LocalSkillBusinessStore();
    }

    @Getter
    @Setter
    @ToString
    private static class Condition {
        private String tenantCode;
        private String businessCode;

        public Condition(String tenantCode, String businessCode) {
            this.tenantCode = tenantCode;
            this.businessCode = businessCode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Condition condition = (Condition) o;
            return tenantCode.equals(condition.tenantCode) &&
                    businessCode.equals(condition.businessCode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tenantCode, businessCode);
        }
    }
}
