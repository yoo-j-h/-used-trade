import React, { useState } from 'react';
import { useTheme } from 'styled-components';
import {
  MainContentArea,
  PageHeader,
  PageTitle,
  StatsCardGrid,
  StatCard,
  StatLabel,
  StatValue,
  StatUnit,
  ControlPanel,
  MonthNavigator,
  NavButton,
  CurrentMonth,
  RefreshButton,
  FilterTabs,
  FilterTab,
  TabIcon,
  TabLabel,
  TabBadge,
  ScheduleTableWrapper,
  ScheduleTable,
  TableHeader,
  TableHeaderCell,
  TableBody,
  TableRow,
  TableCell,
  FlightNumber,
  RouteCode,
  RouteArrow,
  FlightTime,
  Duration,
  StatusBadge,
  PassengerCount,
  CrewSection,
  CrewMember,
  CrewRole,
  CrewName,
  CrewBadge,
  ActionButton,
} from './EmployeeSchedule.styled';

const EmployeeSchedule = () => {
  const theme = useTheme();
  // TODO: Zustand state mapping
  // const { schedules, stats, fetchSchedules } = useScheduleStore();

  const [currentMonth, setCurrentMonth] = useState('2026년 1월');
  const [activeTab, setActiveTab] = useState('assigned');

  // Mock data - Replace with Zustand
  const stats = [
    { label: '전체 항공편', value: 100, unit: '편', color: theme.colors.primary },
    { label: '배정 완료', value: 100, unit: '편', color: theme.status.success },
    { label: '수정 필요', value: 0, unit: '편', color: theme.status.warning },
    { label: '총 직원', value: 310, unit: '명', color: theme.text.secondary },
  ];

  const filterTabs = [
    { id: 'assigned', label: '배정 완료', icon: '✓', count: 100 },
    { id: 'pending', label: '수정 필요', icon: '⚠', count: 0 },
    { id: 'scheduled', label: '직원별 스케줄', icon: '📅', count: 190 },
  ];

  const scheduleData = [
    {
      id: 'KE101',
      route: { from: 'ICN', to: 'AMS' },
      departure: '01/15 06:15',
      duration: '11.4h',
      status: '완결',
      passengers: 270,
      crew: [
        { role: '캡틴', name: '김영수 (CP)', badges: ['김인식', '이서연', '박지원', '+6명'] }
      ]
    },
    {
      id: 'KE102',
      route: { from: 'ICN', to: 'NRT' },
      departure: '01/15 07:30',
      duration: '2h',
      status: '단계별',
      passengers: 250,
      crew: [
        { role: '캡틴', name: '이영호 (CP)', badges: ['원승익', '민서인', '오수빈', '+2명'] }
      ]
    },
    {
      id: 'KE103',
      route: { from: 'ICN', to: 'BKK' },
      departure: '01/15 08:45',
      duration: '5.2h',
      status: '단계별',
      passengers: 290,
      crew: [
        { role: '캡틴', name: '박준형 (CP)', badges: ['최나은', '송지아', '백서준', '+3명'] }
      ]
    },
    {
      id: 'KE104',
      route: { from: 'ICN', to: 'SIN' },
      departure: '01/15 09:45',
      duration: '6.8h',
      status: '단계별',
      passengers: 260,
      crew: [
        { role: '캡틴', name: '최동훈 (CP)', badges: ['안소희', '홍지현', '전민서', '+3명'] }
      ]
    },
    {
      id: 'KE105',
      route: { from: 'ICN', to: 'FCO' },
      departure: '01/15 10:00',
      duration: '11.7h',
      status: '완결',
      passengers: 260,
      crew: [
        { role: '캡틴', name: '정승우 (CP)', badges: ['백수현', '양지혜', '우채원', '+6명'] }
      ]
    },
    {
      id: 'KE106',
      route: { from: 'ICN', to: 'JFK' },
      departure: '01/15 12:30',
      duration: '13.7h',
      status: '완결',
      passengers: 280,
      crew: [
        { role: '캡틴', name: '김민수 (CP)', badges: ['곽준석', '공지명', '민서아', '+6명'] }
      ]
    },
    {
      id: 'KE107',
      route: { from: 'ICN', to: 'SYD' },
      departure: '01/15 13:15',
      duration: '10.6h',
      status: '완결',
      passengers: 290,
      crew: [
        { role: '캡틴', name: '오재민 (CP)', badges: ['박민주', '표지현', '원소영', '+6명'] }
      ]
    },
    {
      id: 'KE108',
      route: { from: 'ICN', to: 'FRA' },
      departure: '01/15 14:45',
      duration: '11h',
      status: '완결',
      passengers: 270,
      crew: [
        { role: '캡틴', name: '유성민 (CP)', badges: ['김소희', '나예은', '단지인', '+6명'] }
      ]
    },
  ];

  const handleMonthChange = (direction) => {
    // TODO: Implement month navigation
    console.log('Month navigation:', direction);
  };

  const handleRefresh = () => {
    // TODO: Implement data refresh with Zustand
    console.log('Refreshing schedule data...');
  };

  const handleEdit = (scheduleId) => {
    // TODO: Navigate to edit page
    console.log('Edit schedule:', scheduleId);
  };

  return (
    <MainContentArea>
      {/* Page Header */}
      <PageHeader>
        <div>
          <PageTitle>직원 스케줄 관리</PageTitle>
        </div>
      </PageHeader>

      {/* Statistics Cards */}
      <StatsCardGrid>
        {stats.map((stat, index) => (
          <StatCard key={index} $color={stat.color}>
            <StatLabel>{stat.label}</StatLabel>
            <div>
              <StatValue>{stat.value}</StatValue>
              <StatUnit>{stat.unit}</StatUnit>
            </div>
          </StatCard>
        ))}
      </StatsCardGrid>

      {/* Control Panel */}
      <ControlPanel>
        <MonthNavigator>
          <NavButton onClick={() => handleMonthChange('prev')}>
            ‹
          </NavButton>
          <CurrentMonth>{currentMonth}</CurrentMonth>
          <NavButton onClick={() => handleMonthChange('next')}>
            ›
          </NavButton>
        </MonthNavigator>

        <RefreshButton onClick={handleRefresh}>
          ↻ 자동 배정 실행
        </RefreshButton>
        <RefreshButton onClick={handleRefresh}>
          ↻ 초기화
        </RefreshButton>
      </ControlPanel>

      {/* Filter Tabs */}
      <FilterTabs>
        {filterTabs.map((tab) => (
          <FilterTab
            key={tab.id}
            $active={activeTab === tab.id}
            onClick={() => setActiveTab(tab.id)}
          >
            <TabIcon>{tab.icon}</TabIcon>
            <TabLabel>{tab.label}</TabLabel>
            <TabBadge $active={activeTab === tab.id}>
              ({tab.count}건)
            </TabBadge>
          </FilterTab>
        ))}
      </FilterTabs>

      {/* Schedule Table */}
      <ScheduleTableWrapper>
        <ScheduleTable>
          <TableHeader>
            <tr>
              <TableHeaderCell>편명</TableHeaderCell>
              <TableHeaderCell>노선</TableHeaderCell>
              <TableHeaderCell>출발시간</TableHeaderCell>
              <TableHeaderCell>비행시간</TableHeaderCell>
              <TableHeaderCell>승객수</TableHeaderCell>
              <TableHeaderCell>배정 승무원</TableHeaderCell>
              <TableHeaderCell>관리</TableHeaderCell>
            </tr>
          </TableHeader>
          <TableBody>
            {scheduleData.map((schedule) => (
              <TableRow key={schedule.id}>
                <TableCell>
                  <FlightNumber>{schedule.id}</FlightNumber>
                </TableCell>
                <TableCell>
                  <RouteCode>{schedule.route.from}</RouteCode>
                  <RouteArrow>→</RouteArrow>
                  <RouteCode>{schedule.route.to}</RouteCode>
                </TableCell>
                <TableCell>
                  <FlightTime>{schedule.departure}</FlightTime>
                </TableCell>
                <TableCell>
                  <Duration>{schedule.duration}</Duration>
                  <StatusBadge $type={schedule.status}>
                    {schedule.status}
                  </StatusBadge>
                </TableCell>
                <TableCell>
                  <PassengerCount>{schedule.passengers}명</PassengerCount>
                </TableCell>
                <TableCell>
                  <CrewSection>
                    {schedule.crew.map((member, idx) => (
                      <CrewMember key={idx}>
                        <CrewRole>{member.role}</CrewRole>
                        <CrewName>{member.name}</CrewName>
                        {member.badges.map((badge, bidx) => (
                          <CrewBadge key={bidx}>{badge}</CrewBadge>
                        ))}
                      </CrewMember>
                    ))}
                  </CrewSection>
                </TableCell>
                <TableCell>
                  <ActionButton onClick={() => handleEdit(schedule.id)}>
                    ✏️ 수정
                  </ActionButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </ScheduleTable>
      </ScheduleTableWrapper>
    </MainContentArea>
  );
};

export default EmployeeSchedule;