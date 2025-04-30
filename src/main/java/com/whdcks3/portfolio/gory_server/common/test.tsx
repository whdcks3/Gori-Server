// import React, {useEffect, useState } from "react";
// import axios from "axios";
// import{Calendar, dateFnsLocalizer} from "react-big-calendar";
// import{DndProvider} from "react-dnd";
// import{HTML5Backend} from "react-dnd-html5-backend";
// import{format, parse, startOfWeek, getDay, differenceInMinutes, addMinutes} from "date-fns";
// import{fetchCourses} from "../../services/courseService";
// import{
//     fetchTeacherSchedules,
//     requestScheduleModification,
//     confirmScheduleModification,
//     cancelScheduleModification,
//     createPersonalSchedule, // 개인 일정 추가 함수 import
//     deletePersonalSchedule,
//     modifyPersonalSchedule,
// } from "../../services/scheduleService";
// import{fetchGoogleCalendars, createGoogleCalendar} from "../../services/googleCalendarService";
// import{getCurrentUser} from "../../services/userService"; // 사용자 정보 가져오기 함수
// import Modal from "../../components/Modal";
// import ko from "date-fns/locale/ko";
// import "react-big-calendar/lib/css/react-big-calendar.css";
// import withDragAndDrop from "react-big-calendar/lib/addons/dragAndDrop";

// const locales = {ko};
// const localizer = dateFnsLocalizer({
//     format,
//     parse,
//     startOfWeek,
//     getDay,
//     locales,
// });

// const DragAndDropCalendar = withDragAndDrop(Calendar);

// const ScheduleTeacher = () =>
// {
//   const[schedules, setSchedules] = useState([]);
//   const[selectedDate, setSelectedDate] = useState(new Date());
//   const[courses, setCourses] = useState([]);                                      // 수강 중인 과목 목록
//   const[selectedCourse, setSelectedCourse] = useState("all");                     // 선택된 과목 필터
//   const[selectedEvent, setSelectedEvent] = useState(null);                        // 선택된 이벤트
//   const[modalOpen, setModalOpen] = useState(false);                               // 모달 상태
//   const[tempEvent, setTempEvent] = useState(null);                                // 수정 중인 이벤트
//   const[currentUser, setCurrentUser] = useState(null);                            // 현재 사용자 정보 저장
//   const[modificationRequests, setModificationRequests] = useState([]);            // 수정 요청 목록
//   const[showModificationRequests, setShowModificationRequests] = useState(false); // 수정 요청 목록의 가시성 상태

//   const[googleCalendars, setGoogleCalendars] = useState([]); // Google 캘린더 목록
//   const[selectedCalendarId, setSelectedCalendarId] = useState("");
//   const[isNewCalendar, setIsNewCalendar] = useState(false);
//   const[newCalendarName, setNewCalendarName] = useState("");
//   const[newCalendarColor, setNewCalendarColor] = useState("#3174ad");

//   const googleCalendarColors = {
//     1 : {code : "#1B887A", name : "청록색"},     // Teal
//     2 : {code : "#2952A3", name : "파란색"},     // Blue
//     3 : {code : "#28754E", name : "녹색"},       // Green
//     4 : {code : "#875509", name : "갈색"},       // Brown
//     5 : {code : "#AB8B00", name : "노란색"},     // Yellow
//     6 : {code : "#BE6D00", name : "주황색"},     // Orange
//     7 : {code : "#B1440E", name : "빨간색"},     // Red
//     8 : {code : "#865A5A", name : "분홍색"},     // Pink
//     9 : {code : "#705770", name : "보라색"},     // Purple
//     10 : {code : "#4E5D6C", name : "회색"},      // Gray
//     11 : {code : "#5A6986", name : "진한 회색"}, // Dark Gray
//   };

//   useEffect(() = > {
//     const loadGoogleCalendars = async() =>
//     {
//       try
//       {
//         const calendars = await fetchGoogleCalendars();
//         setGoogleCalendars(calendars);
//         console.log(calendars);
//       }
//       catch (error)
//       {
//         console.error("Google 캘린더 목록 불러오기 실패:", error);
//       }
//     };
//     loadGoogleCalendars();
//   },
//             []);

//   // 스케줄 데이터를 가져오는 함수
//   const fetchSchedules = async(selectedDate) =>
//   {
//     try
//     {
//       const data = await fetchTeacherSchedules(selectedDate);
//       setSchedules(data);
//       // 수정 요청 목록을 필터링
//       const pendingRequests = data.filter((schedule) = > schedule.modificationRequested);
//       setModificationRequests(pendingRequests);
//     }
//     catch (error)
//     {
//       console.error("스케줄 불러오기 실패:", error);
//     }
//   };

//   // 교강사의 수강 중인 과목 목록 가져오기
//   const fetchTeacherCourses = async() =>
//   {
//     try
//     {
//       const data = await fetchCourses();
//       setCourses(data);
//     }
//     catch (error)
//     {
//       console.error("과목 목록 불러오기 실패:", error);
//     }
//   };

//   useEffect(() = > {
//     const fetchUser = async() =>
//     {
//       try
//       {
//         const {user} = await getCurrentUser(); // 사용자 정보를 가져옴
//         setCurrentUser(user);
//         console.log(user);
//       }
//       catch (error)
//       {
//         console.error("사용자 정보를 불러오는 중 오류 발생:", error);
//       }
//     };

//     fetchUser();
//   },
//             []);

//   useEffect(() = > {
//     fetchSchedules(selectedDate); // 스케줄 가져오기
//     fetchTeacherCourses();        // 수강 중인 과목 가져오기
//   },
//             [selectedDate]);

//   const toggleModificationRequests = () =>
//   {
//     setShowModificationRequests(!showModificationRequests);
//   };

//   // 선택된 날짜가 변경되면 스케줄을 다시 불러옴
//   const handleDateChange = (date) =>
//   {
//     setSelectedDate(date);
//   };

//   // 필터링된 스케줄 목록 반환
//   const getFilteredSchedules = () =>
//   {
//     if (selectedCourse == = "all")
//     {
//       return schedules;
//     }
//     return schedules.filter((schedule) = > !schedule.course || schedule.course._id == = selectedCourse);
//   };

//   // 강의별 색상 구분
//   const eventPropGetter = (event) =>
//   {
//     let backgroundColor = event.color || "#3174ad"; // 강의별로 색상 구분
//     if (event.status == = "pending-modification")
//     {
//       backgroundColor = "#f39c12"; // 수정 요청 중인 일정은 오렌지색
//     }
//     return {style : {backgroundColor}};
//   };

//   // 일정 드래그 후 시간 변경
//   const handleEventDrop = async({event, start, end}) =>
//   {
//     console.log("handleEventDrop");
//     try
//     {
//       // 서버에 일정 수정 요청 전송
//       if (event.course)
//       {
//         await requestScheduleModification(event.id, start, end); // 수정된 API 호출
//         alert("일정 수정 요청이 완료되었습니다.");
//         fetchSchedules(selectedDate); // 스케줄 갱신
//       }
//       else
//       {
//         event.start = start;
//         event.end = end;
//         await handleModifyPersonalEvent(event);
//       }
//     }
//     catch (error)
//     {
//       console.error("일정 수정 요청 실패:", error);
//     }
//   };

//   // 이벤트 클릭 시 모달 열기
//   const handleSelectEvent = (event) =>
//   {
//     console.log(event);
//     if (!event.course)
//     {
//       // 개인 일정인 경우
//       setSelectedEvent(event);
//       setTempEvent({... event});
//       setModalOpen(true); // 모달 열기
//       return;
//     }
//     setSelectedEvent(event);
//     setTempEvent({... event});
//     setModalOpen(true); // 모달 열기
//   };

//   // 수정 요청 취소 버튼 클릭 시 처리
//   const cancelModificationRequest = async(id) =>
//   {
//     try
//     {
//       await cancelScheduleModification(id); // 수정 요청 취소 API 호출
//       alert("수정 요청이 취소되었습니다.");
//       setModalOpen(false);
//       fetchSchedules(selectedDate); // 스케줄 갱신
//     }
//     catch (error)
//     {
//       console.error("수정 취소 실패:", error);
//     }
//   };

//   // 수정 요청 함수
//   const handleModificationRequest = async(event) =>
//   {
//     console.log(event);
//     try
//     {
//       await requestScheduleModification(event.id, event.start, event.end);
//       alert("수정 요청이 완료되었습니다.");
//       setModalOpen(false);
//       fetchSchedules(selectedDate); // 스케줄 갱신
//     }
//     catch (error)
//     {
//       console.error("수정 요청 실패:", error);
//     }
//   };

//   const handleModificationConfirms = async(scheduleId, status) =>
//   {
//     try
//     {
//       await confirmScheduleModification(scheduleId, status);
//       alert(`수정 요청을 ${status ? "승인" : "거절"} 했습니다.`);
//       setModalOpen(false);
//       fetchSchedules(selectedDate); // 스케줄 갱신
//     }
//     catch (error)
//     {
//       console.error("수정 요청 실패:", error);
//     }
//   };

//   // 일정 시간 수정
//   const handleEventChange = (key, value) =>
//   {
//     if (key == = "start")
//     {
//       // 기존 이벤트의 지속 시간(duration)을 계산하여 종료 시간을 자동으로 설정
//       const durationInMinutes = differenceInMinutes(new Date(tempEvent.end), new Date(tempEvent.start));
//       const newEnd = addMinutes(new Date(value), durationInMinutes); // 시작 시간에 지속 시간을 더해 종료 시간 설정

//       setTempEvent((prev) = > ({... prev, start : value, end : newEnd}));
//     }
//     else
//     {
//       setTempEvent((prev) = > ({... prev, [key] : value}));
//     }
//   };

//   // 개인 일정 추가 버튼 클릭 시
//   const handleAddPersonalEventClick = () =>
//   {
//     const initialEvent = {
//       title : "",
//       start : new Date(),
//       end : addMinutes(new Date(), 60), // 기본 1시간 일정
//       course : null,                    // 개인 일정이므로 course는 null
//     };
//     setSelectedEvent(null);
//     setTempEvent(initialEvent);
//     setModalOpen(true); // 모달 열기
//   };

//   // 개인 일정 추가 처리
//   const handleAddPersonalEvent = async(event) =>
//   {
//     try
//     {
//       let googleCalendarId = selectedCalendarId;
//       // 새 캘린더 생성
//       if (isNewCalendar)
//       {
//         try
//         {
//           const newCalendar = await createGoogleCalendar(newCalendarName, newCalendarColor);
//           googleCalendarId = newCalendar.id;
//         }
//         catch (error)
//         {
//           console.error("새 캘린더 생성 실패:", error);
//           return;
//         }
//       }

//       await createPersonalSchedule(tempEvent, googleCalendarId); // 개인 일정 추가 API 호출
//       alert("개인 일정이 추가되었습니다.");
//       fetchSchedules(selectedDate); // 새로 추가된 일정 반영
//       setModalOpen(false);
//     }
//     catch (error)
//     {
//       console.error("개인 일정 추가 실패:", error);
//     }
//   };

//   // 개인 일정 수정 처리
//   const handleModifyPersonalEvent = async(event) =>
//   {
//     try
//     {
//       await modifyPersonalSchedule(event); // 개인 일정 추가 API 호출
//       alert("개인 일정이 수정되었습니다.");
//       fetchSchedules(selectedDate); // 새로 추가된 일정 반영
//       setModalOpen(false);
//     }
//     catch (error)
//     {
//       console.error("개인 일정 추가 실패:", error);
//     }
//   };

//   const handleDeletePersonalEvent = async(scheduleId) =>
//   {
//     try
//     {
//       await deletePersonalSchedule(scheduleId);
//       alert("개인 일정이 삭제되었습니다.");
//       setModalOpen(false);
//       fetchSchedules(selectedDate); // 새로 추가된 일정 반영
//     }
//     catch (error)
//     {
//       console.error("개인 일정 삭제 실패:", error);
//     }
//   };

//   // 일정 추가 및 수정 모달
//   const renderModal = () =>
//   {
//     if (!tempEvent)
//       return null;

//     return (
//         <Modal isOpen = {modalOpen} onClose = {() = > setModalOpen(false)}>
//             <div className = "p-6 bg-gray-800 rounded-lg shadow-lg">
//             <h3 className = "text-xl font-bold mb-4 text-gray-200">{tempEvent.course ? "일정 수정" : "개인 일정 추가"} < / h3 >
//             <h3 className = "text-l font-bold mb-4 text-gray-200">{tempEvent.course ? tempEvent.course.courseName : ""} < / h3 >

//             {!tempEvent.course && !tempEvent.id && (<><div className = "mb-4"><label className = "block text-gray-400"> 일정 제목</ label><input type = "text" value = {tempEvent.title || ""} onChange = {(e) = > setTempEvent({... tempEvent, title : e.target.value})} className = "w-full p-2 border border-gray-700 bg-gray-900 text-gray-300 rounded" /></ div><div className = "mb-4"><label className = "block text-gray-400"> 반복 여부</ label><input type = "checkbox" checked = {tempEvent.repeat || false} onChange = {(e) = > setTempEvent({... tempEvent, repeat : e.target.checked})} className = "mr-2" /><label className = "text-gray-300"> 반복</ label>{tempEvent.repeat && (<input type = "number" placeholder = "반복 횟수 (최대 30)" min = "1" max = "30" value = {tempEvent.repeatCount || ""} onChange = {(e) = > setTempEvent({... tempEvent, repeatCount : e.target.value})} className = "w-full p-2 mt-2 border border-gray-700 bg-gray-900 text-gray-300 rounded" />)} < / div > </>)}

//             < div className = "mb-4" >
//                               <label className = "block text-gray-400"> 시작 시간</ label>
//                               <input
//                                    type = "datetime-local" value = {format(tempEvent.start, "yyyy-MM-dd'T'HH:mm")} onChange = {(e) = > handleEventChange("start", new Date(e.target.value))} className = "w-full p-2 border border-gray-700 bg-gray-900 text-gray-300 rounded" />
//                               </ div>
//                               <div className = "mb-4">
//                               <label className = "block text-gray-400">
//                                   종료 시간</ label>
//                               <input
//                                    type = "datetime-local" value = {format(tempEvent.end, "yyyy-MM-dd'T'HH:mm")} onChange = {(e) = > handleEventChange("end", new Date(e.target.value))} className = "w-full p-2 border border-gray-700 bg-gray-900 text-gray-300 rounded" />
//                               </ div>

//                               {/* 새 Google 캘린더 생성 옵션 */} { !tempEvent.course && !tempEvent.id && (<>
//                                                                                                           <div className = "mb-4">
//                                                                                                           <label className = "block text-gray-400 mb-2">
//                                                                                                               Google 캘린더 선택 : </ label>
//                                                                                                           <select value = {selectedCalendarId} onChange = {(e) = > setSelectedCalendarId(e.target.value)} className = "w-full p-2 border border-gray-700 bg-gray-900 text-gray-300 rounded">
//                                                                                                           <option value = "">
//                                                                                                               기존 Google 캘린더를 선택하세요</ option>{googleCalendars.map((calendar) = > (<option key = {calendar.id} value = {calendar.id}>{calendar.summary} < / option >))} < /
//                                                                                                           select >
//                                                                                                           </ div>
//                                                                                                           <div className = "mb-4">
//                                                                                                           <label className = "block text-gray-400 mb-2">
//                                                                                                           <input type = "checkbox" checked = {isNewCalendar} onChange = {(e) = > setIsNewCalendar(e.target.checked)} className = "mr-2" />
//                                                                                                               새 Google 캘린더 생성</ label>{isNewCalendar && (<>
//                                                                                                                                                                <input
//                                                                                                                                                                     type = "text" placeholder = "새 캘린더 이름" value = {newCalendarName} onChange = {(e) = > setNewCalendarName(e.target.value)} className = "w-full mt-2 p-2 border border-gray-700 bg-gray-900 text-gray-300 rounded" />
//                                                                                                                                                                <div className = "flex items-center mt-2">
//                                                                                                                                                                <label className = "block text-gray-400 mr-4">
//                                                                                                                                                                    색상 선택 < span className = "w-6 h-6 rounded-full inline-block mr-2" style = {{backgroundColor : googleCalendarColors[newCalendarColor] ?.code }} > </ span>
//                                                                                                                                                                </ label>
//                                                                                                                                                                <select value = {newCalendarColor} onChange = {(e) = > setNewCalendarColor(e.target.value)} className = "w-full p-2 border border-gray-700 bg-gray-900 text-gray-300 rounded">
//                                                                                                                                                                <option value = "">
//                                                                                                                                                                    Google 캘린더 색상을 선택하세요</ option>{Object.entries(googleCalendarColors).map(([ colorId, colorInfo ]) = > (<option key = {colorId} value = {colorId} style = {{color : colorInfo.code}}>{colorInfo.name} < / option >))} < /
//                                                                                                                                                                select >
//                                                                                                                                                                </ div>
//                                                                                                                                                                </>) }</ div>
//                                                                                                           </>) }

//                               {tempEvent.course ? (
//                                                       renderModificationButtons())
//                                                 : (
//                                                       <div className = "flex space-x-4">{!tempEvent.id && (<button onClick = {() = > handleAddPersonalEvent(tempEvent)} className = "w-full py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition">
//                                                                                                                개인 일정 추가</ button>)} { tempEvent.id && (<button onClick = {() = > handleModifyPersonalEvent(tempEvent)} className = "w-full py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition">
//                                                                                                                                                                  개인 일정 수정</ button>) } {tempEvent.id && !tempEvent.course && (<button onClick = {() = > handleDeletePersonalEvent(tempEvent.id)} className = "w-full py-2 bg-red-600 text-white rounded hover:bg-red-700 transition"> 개인 일정 삭제</ button>)} < / div >)} < / div >
//                               </ Modal>);
//   };

//   // 수정 요청 리스트를 화면에 표시
//   const renderModificationRequests = () =>
//   {
//     if (modificationRequests.length == = 0)
//     {
//       return <p className = "text-center text-gray-400"> 수정 요청이 없습니다.</ p>;
//     }

//     return (
//         <ul className = "divide-y divide-gray-700">{modificationRequests.map((request) = > {
//           const isRequester = request.modifiedBy == = currentUser._id;
//           return (
//               <li key = {request._id} className = "py-4 flex items-center justify-between">
//               <div>
//               <h3 className = "text-lg font-semibold text-gray-200">{request.course ? request.course.courseName : request.title || "개인 일정"} < / h3 >
//               <p className = "text-sm text-gray-400">
//                   <del>{format(new Date(request.originalStart), "yyyy-MM-dd HH:mm")} -
//                   {format(new Date(request.originalEnd), "yyyy-MM-dd HH:mm")} <
//               / del >
//               </ p>
//                   <p className = "text-sm text-gray-400">{format(new Date(request.start), "yyyy-MM-dd HH:mm")} -
//                   {format(new Date(request.end), "yyyy-MM-dd HH:mm")} <
//               / p >
//               <p className = "text-sm text-gray-400">{isRequester ? "내가 요청함" : "상대방이 요청함"} < / p >
//               </ div>
//               <div className = "flex space-x-2">{!isRequester && (<>
//                                                                   <button onClick = {() = > handleModificationConfirms(request._id, true)} className = "bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700 transition">
//                                                                       승인</ button>
//                                                                   <button onClick = {() = > handleModificationConfirms(request._id, false)} className = "bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700 transition">
//                                                                       거절</ button>
//                                                                   </>)} {isRequester && (<button onClick = {() = > cancelModificationRequest(request._id)} className = "bg-gray-600 text-white px-4 py-2 rounded hover:bg-gray-700 transition">
//                                                                                              수정 취소</ button>)} < / div >
//               </ li>);
//         })} < / ul >);
//   };

//   // 버튼 조건부 렌더링: 수정 요청에 따라 버튼을 다르게 표시
//   const renderModificationButtons = () =>
//   {
//     if (!tempEvent || !currentUser)
//       return null;

//     const isRequester = tempEvent.modifiedBy == = currentUser._id; // 수정 요청자와 현재 사용자 비교
//     const isRecipient = tempEvent.modifiedBy != = currentUser._id; // 수정 요청을 받은 사람

//     if (tempEvent.modificationRequested)
//     {
//       if (isRequester)
//       {
//         // 수정 요청을 한 사람
//         return (
//             <>
//             <button onClick = {() = > cancelModificationRequest(tempEvent._id)} className = "w-full py-2 bg-red-600 text-gray-200 rounded hover:bg-red-700 transition">
//                 수정 취소</ button>
//             <button onClick = {() = > handleModificationRequest(tempEvent)} className = "w-full py-2 bg-blue-600 text-gray-200 rounded hover:bg-blue-700 mt-4 transition">
//                 재수정</ button>
//             </>);
//       }
//       else if (isRecipient)
//       {
//         // 수정 요청을 받은 사람
//         return (
//             <>
//             <button onClick = {() = > handleModificationConfirms(tempEvent.id, true)} className = "w-full py-2 bg-green-600 text-gray-200 rounded hover:bg-green-700 transition">
//                 수정 승인</ button>
//             <button onClick = {() = > handleModificationConfirms(tempEvent.id, false)} className = "w-full py-2 bg-red-600 text-gray-200 rounded hover:bg-red-700 mt-4 transition">
//                 수정 거절</ button>
//             </>);
//       }
//     }
//     else
//     {
//       // 수정 요청이 없는 경우 수정 요청 버튼
//       return (
//           <button onClick = {() = > handleModificationRequest(tempEvent)} className = "w-full py-2 bg-blue-600 text-gray-200 rounded hover:bg-blue-700 transition">
//               수정 요청</ button>);
//     }
//   };

//   return (
//     <DndProvider backend={HTML5Backend}>
//       <div className="mx-auto p-6 bg-gray-900 text-gray-200 min-h-screen">
//         <h2 className="text-3xl font-bold mb-6 text-white">스케줄 관리</h2>

//         {/* 개인 일정 추가 버튼 */}
//         <div className="mb-4">
//           <button onClick={handleAddPersonalEventClick} className="bg-green-600 text-white hover:bg-green-700 font-semibold py-2 px-4 rounded-lg shadow transition duration-300">
//             개인 일정 추가
//           </button>
//         </div>

//         {/* 필터링 UI */}
//         <div className="mb-4">
//           <label className="block text-gray-400 font-semibold mb-2">과목별 보기</label>
//           <select value={selectedCourse} onChange={(e) => setSelectedCourse(e.target.value)} className="w-full p-2 bg-gray-800 border border-gray-600 rounded text-gray-200">
//             <option value="all">전체보기</option>
//             {courses.map((course) => (
//               <option key={course._id} value={course._id}>
//                 {course?.courseName || "개인 일정"}
//               </option>
//             ))}
//           </select>
//         </div>

//         <div className="flex justify-end mb-4">
//           <button onClick={toggleModificationRequests} className="bg-blue-600 text-white hover:bg-blue-700 font-semibold py-2 px-4 rounded-lg shadow transition duration-300 focus:outline-none">
//             {showModificationRequests ? "수정 요청 목록 숨기기" : "수정 요청 목록 보기"}
//           </button>
//         </div>

//         <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
//           {/* 수정 요청 리스트 */}
//           {
//     showModificationRequests && (<div className = "bg-gray-800 shadow-lg rounded-lg p-6">
//                                  <h3 className = "text-xl font-semibold mb-4 text-gray-100">
//                                      수정 요청 목록</ h3>{renderModificationRequests()} < / div >)}

//           {/* 캘린더 */}
//           <div className={`bg-gray-800 shadow-lg rounded-lg p-6 ${showModificationRequests ? "lg:col-span-1" : "lg:col-span-2"}`}>
//             <DragAndDropCalendar
//               localizer={localizer}
//               events={getFilteredSchedules().map((schedule) => ({
//                 title: `${schedule.course?.courseName || schedule.title || "개인 일정"}`,
//                 start: new Date(schedule.start),
//                 end: new Date(schedule.end),
//                 originalStart: schedule.originalStart,
//                 originalEnd: schedule.originalEnd,
//                 id: schedule._id,
//                 status: schedule.status, // 일정 상태 추가
//                 color: schedule.course?.colorCode || "#ccc2d6",
//                 course: schedule.course,
//                 modifiedBy: schedule.modifiedBy,
//                 modificationRequested: schedule.modificationRequested,
//               }))}
//               startAccessor="start"
//               endAccessor="end"
//               style={
//   {height : 800}}
//               eventPropGetter={eventPropGetter}
//               onNavigate={handleDateChange} // 달력 날짜 이동 시 호출
//               onSelectEvent={handleSelectEvent} // 일정 클릭 시 호출
//               onEventDrop={handleEventDrop} // 일정 드래그 후 호출
//               draggableAccessor={() => true} // 이벤트 드래그 가능하도록 설정
//             />
//           </div>
//         </div>
//         {renderModal()}
//       </div>
//     </DndProvider>
//   );
// };

// export default ScheduleTeacher;
