import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import * as S from "./FlightSchedule.styled";
import { flightScheduleService } from "../../api/flightSchedule/services";
import { airportService } from "../../api/airport/services";
import useAuthStore from "../../store/authStore";

const FlightSchedule = () => {
  const navigate = useNavigate();
  const { getRole, emp } = useAuthStore();
  const userRole = getRole();
  const isAdmin = userRole === 'AIRLINE_ADMIN' || userRole === 'ADMIN' || userRole === 'SUPER_ADMIN';
  const airlineId = emp?.airlineId;

  const [selectedDate, setSelectedDate] = useState(new Date());
  const [departureCity, setDepartureCity] = useState("all");
  const [arrivalCity, setArrivalCity] = useState("all");
  const [flightList, setFlightList] = useState([]);
  const [loading, setLoading] = useState(false);
  const [airports, setAirports] = useState([]);
  const [availableDepartures, setAvailableDepartures] = useState([]);
  const [availableDestinations, setAvailableDestinations] = useState([]);

  // 비행편 목록 조회 (날짜 변경 시 자동 검색)
  useEffect(() => {
    loadFlightSchedules();
  }, [selectedDate]);

  // 공항 목록 조회 (초기 로드 시)
  useEffect(() => {
    loadAirports();
  }, []);

  // 비행편 목록이 변경되면 출발지/도착지 목록 업데이트
  useEffect(() => {
    updateAvailableAirports();
  }, [flightList]);

  // 공항 목록 조회
  const loadAirports = async () => {
    try {
      const response = await airportService.getAirports();
      const airportList = response.data?.data || response.data || [];
      setAirports(airportList);
    } catch (error) {
      console.error('공항 목록 조회 실패:', error);
    }
  };

  const loadFlightSchedules = async () => {
    try {
      setLoading(true);
      const params = {};

      // 관리자인 경우 항공사 ID 추가
      if (isAdmin && airlineId) {
        params.airlineId = airlineId;
      }

      // 날짜 필터 추가 (선택한 날짜의 00:00:00 ~ 다음 날 00:00:00)
      // 출발 시간(flyStartTime)을 기준으로 해당 날짜의 모든 비행편 조회
      if (selectedDate) {
        const startDate = new Date(selectedDate);
        startDate.setHours(0, 0, 0, 0);
        const endDate = new Date(selectedDate);
        endDate.setDate(endDate.getDate() + 1); // 다음 날 00:00:00
        endDate.setHours(0, 0, 0, 0);

        // ISO 8601 형식으로 변환
        params.startDate = startDate.toISOString();
        params.endDate = endDate.toISOString();
      }

      // 출발지/도착지 필터 ("all"이 아닐 때만 파라미터 추가)
      if (departureCity && departureCity !== "all") {
        params.departure = departureCity;
      }
      if (arrivalCity && arrivalCity !== "all") {
        params.destination = arrivalCity;
      }

      const response = await flightScheduleService.getFlightSchedules(params);
      const flights = response.data?.data || response.data || [];

      // 디버깅: 응답 데이터 확인
      console.log('비행편 목록 응답:', response.data);
      console.log('비행편 목록:', flights);
      if (flights.length > 0) {
        console.log('첫 번째 비행편 데이터:', flights[0]);
        console.log('첫 번째 비행편 flyScheduleId:', flights[0].flyScheduleId || flights[0].fly_schedule_id);
        console.log('첫 번째 비행편 airlineName:', flights[0].airlineName || flights[0].airline_name);
      }

      setFlightList(flights);
    } catch (error) {
      console.error('비행편 목록 조회 실패:', error);
      alert('비행편 목록을 불러오는데 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  // 비행편 목록에서 출발지/도착지 추출
  const updateAvailableAirports = async () => {
    try {
      // 먼저 공항 목록 조회
      const airportResponse = await airportService.getAirports();
      const airportList = airportResponse.data?.data || airportResponse.data || [];
      setAirports(airportList);

      // 비행편 목록에서 출발지/도착지 추출
      const departures = [...new Set(flightList.map(flight => flight.departure).filter(Boolean))];
      const destinations = [...new Set(flightList.map(flight => flight.destination).filter(Boolean))];

      // 공항 정보와 매칭하여 정렬
      const departureAirports = departures
        .map(code => airportList.find(airport => airport.airportCode === code))
        .filter(Boolean)
        .sort((a, b) => (a.airportName || a.airportCode).localeCompare(b.airportName || b.airportCode));

      const destinationAirports = destinations
        .map(code => airportList.find(airport => airport.airportCode === code))
        .filter(Boolean)
        .sort((a, b) => (a.airportName || a.airportCode).localeCompare(b.airportName || b.airportCode));

      setAvailableDepartures(departureAirports);
      setAvailableDestinations(destinationAirports);
    } catch (error) {
      console.error('공항 정보 업데이트 실패:', error);
    }
  };

  const handleSearch = () => {
    loadFlightSchedules();
  };

  // 날짜 포맷팅 (안전하게 처리)
  const formatDate = (dateTimeString) => {
    if (!dateTimeString) return '';
    try {
      const date = new Date(dateTimeString);
      // 유효한 날짜인지 확인
      if (isNaN(date.getTime())) {
        return '';
      }
      const days = ['일', '월', '화', '수', '목', '금', '토'];
      const month = date.getMonth() + 1;
      const day = date.getDate();
      const dayName = days[date.getDay()];
      return `${month}월 ${day}일 (${dayName})`;
    } catch (error) {
      console.error('날짜 포맷팅 오류:', error);
      return '';
    }
  };

  // 공항 코드로 공항명 찾기
  const getAirportName = (airportCode) => {
    if (!airportCode) return '';
    const airport = airports.find(a => a.airportCode === airportCode);
    return airport ? (airport.airportName || airport.cityName || airportCode) : airportCode;
  };

  // 비행 상태 한글 변환
  const getStatusText = (status) => {
    const statusMap = {
      'DELAYED': '지연',
      'CANCELLED': '취소',
      'DEPARTED': '출발',
      'ARRIVED': '도착',
      'ASSIGNING': '배정중',
      'ASSIGNED': '배정완료'
    };
    return statusMap[status] || status;
  };

  // ✅ 필터링 로직: 관리자는 전체, 직원은 본인 배정 비행편만
  const displayedFlights = isAdmin
    ? flightList
    : flightList.filter(flight => flight.isAssignedToMe);

  return (
    <S.PageContainer>
      {/* Page Header */}
      <S.PageHeader>
        <S.HeaderLeft>
          <S.PageTitle>{isAdmin ? '전체 비행편 및 크루 관리' : '나의 비행 일정'}</S.PageTitle>
          <S.PageSubtitle>
            {isAdmin
              ? '모든 비행편의 운항 정보와 배정된 크루 현황을 관리합니다.'
              : '내가 배정된 비행 일정을 확인하고 상세 정보를 조회합니다.'}
          </S.PageSubtitle>
        </S.HeaderLeft>
      </S.PageHeader>

      {/* Search Filter Section */}
      <S.FilterSection>
        <S.FilterGroup>
          <S.FilterLabel>날짜 선택</S.FilterLabel>
          <S.DateInput
            type="date"
            value={selectedDate.toISOString().split('T')[0]}
            onChange={(e) => setSelectedDate(new Date(e.target.value))}
          />
        </S.FilterGroup>

        <S.FilterGroup>
          <S.FilterLabel>출발지</S.FilterLabel>
          <S.CitySelect
            value={departureCity}
            onChange={(e) => setDepartureCity(e.target.value)}
          >
            <option value="all">전체</option>
            {availableDepartures.map((airport) => (
              <option key={airport.airportCode} value={airport.airportCode}>
                {airport.airportCode} - {airport.airportName || airport.cityName || airport.airportCode}
              </option>
            ))}
          </S.CitySelect>
        </S.FilterGroup>

        <S.FilterGroup>
          <S.FilterLabel>도착지</S.FilterLabel>
          <S.CitySelect
            value={arrivalCity}
            onChange={(e) => setArrivalCity(e.target.value)}
          >
            <option value="all">전체</option>
            {availableDestinations.map((airport) => (
              <option key={airport.airportCode} value={airport.airportCode}>
                {airport.airportCode} - {airport.airportName || airport.cityName || airport.airportCode}
              </option>
            ))}
          </S.CitySelect>
        </S.FilterGroup>

        <S.SearchButton type="button" onClick={handleSearch}>검색</S.SearchButton>
      </S.FilterSection>

      {/* Flight List */}
      <S.FlightListContainer>
        {loading ? (
          <S.MessageContainer>
            로딩 중...
          </S.MessageContainer>
        ) : displayedFlights.length > 0 ? (
          displayedFlights.map((flight) => {
            // flyScheduleId 확인 (snake_case 또는 camelCase 모두 지원)
            const flyScheduleId = flight.flyScheduleId || flight.fly_schedule_id;
            const airlineName = flight.airlineName || flight.airline_name;

            if (!flyScheduleId) {
              console.warn('비행편에 flyScheduleId가 없습니다:', flight);
            }

            return (
              <S.FlightCard
                key={flyScheduleId}
                onClick={() => {
                  console.log('비행편 클릭 - flyScheduleId:', flyScheduleId);
                  if (flyScheduleId) {
                    navigate(`/flightschedule/${flyScheduleId}`);
                  }
                }}
              >
                <S.CardHeader>
                  <S.FlightBadge>
                    <S.AirlineIcon>✈</S.AirlineIcon>
                    <div>
                      <S.FlightNumber>
                        {flight.flightNumber || flight.flight_number}
                        {!isAdmin && (flight.isAssignedToMe || flight.is_assigned_to_me) && (
                          <span style={{ fontSize: '0.8em', marginLeft: '8px', color: '#4a90e2' }}>● 배정됨</span>
                        )}
                      </S.FlightNumber>
                      <S.FlightDate>
                        {formatDate(flight.flyStartTime || flight.fly_start_time)} • {airlineName && airlineName !== '.' ? airlineName : '항공사'}
                      </S.FlightDate>
                    </div>
                  </S.FlightBadge>

                  <S.StatusBadgeGroup>
                    <S.StatusBadge $status="normal">
                      운항 {getStatusText(flight.flightStatus || flight.flight_status)}
                    </S.StatusBadge>
                    {(flight.crewAssigned || flight.crew_assigned) && (
                      <S.StatusBadge $status="assigned">
                        승무원 배정
                      </S.StatusBadge>
                    )}
                  </S.StatusBadgeGroup>
                </S.CardHeader>

                <S.FlightRoute>
                  <S.RoutePoint>
                    <S.RouteTime>{flight.departureTime || flight.departure_time || ''}</S.RouteTime>
                    <S.RouteCode>{flight.departure}</S.RouteCode>
                    <S.RouteAirport>{getAirportName(flight.departure)}</S.RouteAirport>
                  </S.RoutePoint>

                  <S.RouteIndicator>
                    <S.AirplaneIcon>✈</S.AirplaneIcon>
                    <S.RouteLine />
                    <S.RouteDuration>{flight.duration || ''}</S.RouteDuration>
                  </S.RouteIndicator>

                  <S.RoutePoint>
                    <S.RouteTime>{flight.arrivalTime || flight.arrival_time || ''}</S.RouteTime>
                    <S.RouteCode>{flight.destination}</S.RouteCode>
                    <S.RouteAirport>{getAirportName(flight.destination)}</S.RouteAirport>
                  </S.RoutePoint>
                </S.FlightRoute>
              </S.FlightCard>
            );
          })
        ) : (
          <S.MessageContainer>
            배정된 비행 일정이 없거나 조회 결과가 없습니다.
          </S.MessageContainer>
        )}
      </S.FlightListContainer>
    </S.PageContainer>
  );
};

export default FlightSchedule;
