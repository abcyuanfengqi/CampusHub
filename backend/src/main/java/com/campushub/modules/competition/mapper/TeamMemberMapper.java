package com.campushub.modules.competition.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campushub.modules.competition.entity.TeamMember;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TeamMemberMapper extends BaseMapper<TeamMember> {
}
