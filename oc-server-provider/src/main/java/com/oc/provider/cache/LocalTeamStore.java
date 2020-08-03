package com.oc.provider.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.oc.domain.team.Team;
import com.oc.domain.team.TeamSkill;
import com.oc.provider.context.SpringContext;
import com.oc.service.team.TeamService;
import com.oc.service.team.TeamSkillService;
import com.oc.service.team.impl.TeamServiceImpl;
import com.oc.service.team.impl.TeamSkillServiceImpl;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 团队功能数据缓存
 * @author chuangyeifang
 * @createDate 2020年6月19日
 * @version v 1.0
 */
@Slf4j
public class LocalTeamStore {
	
	private TeamService teamService = SpringContext.getBean(TeamServiceImpl.class);
	private TeamSkillService teamSkillService = SpringContext.getBean(TeamSkillServiceImpl.class);

	private LoadingCache<Integer, Team> teamCache;
	private LoadingCache<TeamSkillSearchCondition, TeamSkill> teamSkillCache;

	private LocalTeamStore() {
		initCache();
	}
	
	private void initCache() {
		// 缓存过期时间（秒）
		long expireOfSeconds = 60 * 5;
		teamCache = CacheBuilder.newBuilder()
				.expireAfterWrite(expireOfSeconds, TimeUnit.SECONDS)
				.build(new CacheLoader<Integer, Team>(){

					@Override
					@ParametersAreNonnullByDefault
					public Team load(Integer teamCode) {
						return teamService.obtainTeam(teamCode);
					}
				});
		
		teamSkillCache = CacheBuilder.newBuilder()
				.expireAfterWrite(expireOfSeconds, TimeUnit.SECONDS)
				.build(new CacheLoader<TeamSkillSearchCondition, TeamSkill>() {

					@Override
					public TeamSkill load(TeamSkillSearchCondition condition) {
						return teamSkillService.obtainTeamSkill(condition.getTenantCode(), condition.getSkillCode());
					}
				});
	}

	/**
	 * 根据团队编码获取团队信息
	 * @param teamCode
	 * @return
	 */
	public Team getTeam(Integer teamCode) {
		try {
			return teamCache.get(teamCode);
		} catch (ExecutionException e) {
			log.error("根据团队编码：{}, 获取团队信息发生异常：{}", teamCode, e);
		}
		return null;
	}

	public String getSkillName(String tenantCode, Integer skillCode) {
		TeamSkillSearchCondition condition = new TeamSkillSearchCondition(tenantCode, skillCode);
		try {
			TeamSkill teamSkill = teamSkillCache.get(condition);
			if (null != teamSkill) {
				return teamSkill.getSkillName();
			}
		} catch (ExecutionException e) {
			log.error("获取团队技能为空 condition: {}", condition);
		}
		return null;
	}
	
	/**
	 * 分配规则 0 记忆分配 1轮训分配 2空闲分配
	 * 如果未配置则默认为：记忆分配
	 * @param teamCode
	 * @return
	 */
	public String getTeamAssignRule(Integer teamCode) {
		String result = "0";
		try {
			Team team = teamCache.get(teamCode);
			if (null != team) {
				result = StringUtils.isEmpty(team.getAssignRule()) ? "0" : team.getAssignRule();
			}
		} catch (ExecutionException e) {
			log.error("根据团队编码：{}, 获取团队信息发生异常：{}", teamCode, e);
		}
		return result;
	}
	
	/**
	 * 根据团队编码获取当前团队自动回复语
	 * 如果开启自动回复，则返回自动回复语。
	 * 否则返回null
	 * @param teamCode
	 * @return
	 */
	public String getTeamReply(Integer teamCode) {
		String replyMsg = null;
		String autoReply = "1";
		try {
			Team team = teamCache.get(teamCode);
			if (null != team && autoReply.equals(team.getAutoReply())) {
				replyMsg = team.getReplyMsg();
			}
		} catch (ExecutionException e) {
			log.error("根据团队编码：{}, 获取自动回复语发生异常：{}", teamCode, e);
		}
		return replyMsg;
	}

	public static LocalTeamStore getInst() {
		return Single.instance;
	}
	
	private static class Single {
		private static LocalTeamStore instance = new LocalTeamStore();
	}

	@Getter
	@Setter
	@ToString
	private static class TeamSkillSearchCondition {

		private String tenantCode;

		private Integer skillCode;

		public TeamSkillSearchCondition(String tenantCode, Integer skillCode) {
			this.tenantCode = tenantCode;
			this.skillCode = skillCode;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			TeamSkillSearchCondition that = (TeamSkillSearchCondition) o;
			return Objects.equals(tenantCode, that.tenantCode) &&
					Objects.equals(skillCode, that.skillCode);
		}

		@Override
		public int hashCode() {
			return Objects.hash(tenantCode, skillCode);
		}
	}
}
