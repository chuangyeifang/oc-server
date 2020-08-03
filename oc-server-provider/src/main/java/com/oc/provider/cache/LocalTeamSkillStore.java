package com.oc.provider.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.oc.domain.team.TeamSkill;
import com.oc.provider.context.SpringContext;
import com.oc.service.team.TeamSkillService;
import com.oc.service.team.impl.TeamSkillServiceImpl;
import lombok.AllArgsConstructor;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author chuangyeifang
 */
public class LocalTeamSkillStore {

    private TeamSkillService teamSkillService = SpringContext.getBean(TeamSkillServiceImpl.class);

    private static LoadingCache<Condition, Optional<TeamSkill>> teamSkillCache;

    private LocalTeamSkillStore() {
        initCache();
    }

    private void initCache() {
        teamSkillCache = CacheBuilder.newBuilder()
                .refreshAfterWrite(5, TimeUnit.MINUTES)
                .maximumSize(500)
                .build(new CacheLoader<Condition, Optional<TeamSkill>>() {
                    @Override
                    @ParametersAreNonnullByDefault
                    public Optional<TeamSkill> load(Condition condition) {
                       return Optional.ofNullable(teamSkillService.obtainTeamSkill(condition.tenantCode, condition.skillCode));
                    }
                });
    }

    public TeamSkill get(String tenantCode, Integer skillCode) {
        try {
            Condition condition = new Condition(tenantCode, skillCode);
            return teamSkillCache.get(condition).orElse(null);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static LocalTeamSkillStore getInst() {
        return Single.instance;
    }

    private static class Single {
        private static LocalTeamSkillStore instance = new LocalTeamSkillStore();
    }

    @AllArgsConstructor
    private static class Condition {
        private String tenantCode;
        private Integer skillCode;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Condition condition = (Condition) o;
            return Objects.equals(tenantCode, condition.tenantCode) &&
                    Objects.equals(skillCode, condition.skillCode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tenantCode, skillCode);
        }
    }
}
