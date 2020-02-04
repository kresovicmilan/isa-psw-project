package com.proj.medicalClinic.service.implementation;

import com.proj.medicalClinic.dto.*;
import com.proj.medicalClinic.exception.NotExistsException;
import com.proj.medicalClinic.exception.ResourceConflictException;
import com.proj.medicalClinic.model.AdminClinic;
import com.proj.medicalClinic.model.Appointment;
import com.proj.medicalClinic.exception.NotValidParamsException;
import com.proj.medicalClinic.model.*;
import com.proj.medicalClinic.repository.*;
import com.proj.medicalClinic.service.AppointmentService;
import com.proj.medicalClinic.service.OperationRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private ExaminationRepository examinationRepository;

    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private OperationRoomRepository operationRoomRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private OperationRoomService operationRoomService;

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private ServiceRepository serviceRepository;


    @Override
    public List<AppointmentDTO> getAllByOperationRoom(Long id){
        List<Appointment> appointments = appointmentRepository.findAllByOperationRoomId(id)
                .orElse(null);

        List<AppointmentDTO> appointmentDTOS = new ArrayList<>();

        if(appointments != null){
            for(Appointment a : appointments){
                appointmentDTOS.add(new AppointmentDTO(a));
            }
        }

        return appointmentDTOS;
    }

    @Override
    public List<AppointmentHistoryDTO> getAllByPatient(Long id) {
        List<Appointment> appointments = appointmentRepository.findAllByPatientId(id)
                .orElseThrow(NotExistsException::new);

        List<AppointmentHistoryDTO> appointmentDTOS = new ArrayList<>();

        for(Appointment a : appointments){
            appointmentDTOS.add(new AppointmentHistoryDTO(a));
        }

        return appointmentDTOS;
    }

    @Override
    public List<AppointmentDTO> getAllAppointmentRequests() {

        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        String username = currentUser.getName();

        AdminClinic adminClinic = (AdminClinic) appUserRepository.findByEmail(username).orElseThrow(NotExistsException::new);

        Long clinicId = adminClinic.getClinic().getId();

        List<Appointment> appointments = appointmentRepository.findAllAppointmentRequests(clinicId).orElseThrow(NotExistsException::new);
        List<AppointmentDTO> appointmentDTOS = new ArrayList<>();
        for (Appointment a : appointments) {
            appointmentDTOS.add(new AppointmentDTO(a));
        }

        return appointmentDTOS;
    }

    @Override
    public List<AppointmentHistoryDTO> getAllAppointmentsByMedicalStaffMember(String email) {
        try {
            AppUser user = appUserRepository.findByEmail(email)
                    .orElseThrow(NotExistsException::new);

            if (!(user instanceof Doctor || user instanceof Nurse)) {
                throw new NotValidParamsException("Only medical staff members can see this data");
            }
            
            if (user instanceof Nurse) {
                System.out.println(email);
                System.out.println(user.getId());
                List<Appointment> appointments = appointmentRepository.findAllByNurse(user.getId());

                for(Appointment ap: appointments) {
                    System.out.println(ap.getId());
                }

                List<AppointmentHistoryDTO> appointmentHistoryDTO = appointments.stream().map(
                        s -> new AppointmentHistoryDTO(s)
                ).collect(Collectors.toList());

                return appointmentHistoryDTO;
            } else {
                List<Examination> examinations = examinationRepository.findAllByDoctorsContaining((Doctor) user);

                List<AppointmentHistoryDTO> appointmentHistoryDTO = examinations.stream().map(
                        s -> new AppointmentHistoryDTO(s)
                ).collect(Collectors.toList());

                List<Operation> operations = operationRepository.findAllByDoctorsContaining((Doctor) user);

                List<AppointmentHistoryDTO> appointmentHistoryDTO1 = operations.stream().map(
                        s -> new AppointmentHistoryDTO(s)
                ).collect(Collectors.toList());

                appointmentHistoryDTO.addAll(appointmentHistoryDTO1);
                return appointmentHistoryDTO;
            }
        } catch (NotExistsException | NotValidParamsException e) {
            throw e;
        }
    }

    @Override
    public List<Appointment> getAllDayBeforeAndDayAfter(Date before, Date after) {

        List<Appointment> appointments = appointmentRepository.findAllByDateBetweenAndOperationRoomIsNotNull(before, after);
        if(appointments.isEmpty()){
            return null;
        }
        return appointments;
    }

    @Override
    public AppointmentDTO addRoom(Long appointmentId, Long roomId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(NotExistsException::new);
        OperationRoom operationRoom = operationRoomRepository.findById(roomId).orElseThrow(NotExistsException::new);

        appointment.setOperationRoom(operationRoom);
        appointmentRepository.save(appointment);

        return new AppointmentDTO(appointment);
    }

    @Override
    public AppointmentDTO changeDateAndAddRoom(AppointmentRequestDTO appointmentRequestDTO) {
        Appointment appointment = appointmentRepository.findById(appointmentRequestDTO.getAppId()).orElseThrow(NotExistsException::new);
        OperationRoom operationRoom = operationRoomRepository.findById(appointmentRequestDTO.getRoomId()).orElseThrow(NotExistsException::new);

        long selectedDateMilisecds = appointmentRequestDTO.getStart();
        Date selectedDate = new Date(selectedDateMilisecds);


        if (appointment instanceof  Examination){
            Examination examination = (Examination) appointment;
            List<Doctor> doctors = doctorRepository.findAllByExaminations(examination);

            if(doctors == null){
                throw new NotExistsException();
            }

            Doctor currentDoctor = doctors.get(0);

            for(Examination ex : currentDoctor.getExaminations()){
                long exStart = ex.getDate().getTime();
                long exEnd = (long) (exStart + ex.getDuration() * 60000);

                System.out.println("---------------");
                System.out.println(ex.getId());
                System.out.println(exStart + " PRE");
                System.out.println(selectedDateMilisecds + " SELEKTOVAN");
                System.out.println(exEnd + " POSLE");
                System.out.println("---------------");

                if(selectedDateMilisecds >= exStart && selectedDateMilisecds <= exEnd){
                    System.out.println("USAO VAMO");
                    throw new ResourceConflictException(ex.getId(), "Doktor " + currentDoctor.getName() + " vec ima zakazan pregled u ovo vreme");
                }
            }
            appointment.setDate(selectedDate);
            appointment.setOperationRoom(operationRoom);
            appointmentRepository.save(appointment);
            return new AppointmentDTO(appointment);
        }
        return null;
    }

    public void cronAddRooms(){


        List<Appointment> unnaprovedRequests = appointmentRepository.findAllByOperationRoomIsNull();
        if(unnaprovedRequests.isEmpty() || unnaprovedRequests == null){
            System.out.println("NEMA ZAHTEVA");
            return;
        }

        List<OperationRoom> operationRooms = operationRoomRepository.findAllByDeletedNot(true);
        if(operationRooms.isEmpty() || operationRooms == null){
            System.out.println("NEMA SOBA");
            return;
        }

        for(Appointment unnaprovedApp : unnaprovedRequests){

            if(unnaprovedApp instanceof Examination){
                //UZMI SVE SLOBODNE SOBE ZA TAJ DATUM
                List<OperationRoomDTO> availableRooms = operationRoomService.getAllAvailable(unnaprovedApp.getDate().getTime());
                if(availableRooms.isEmpty() || availableRooms == null){
                    return;
                }

                //PROVERI DA JE APPOINTMENT I SOBA IZ ISTE KLINIKE
                for(OperationRoomDTO or : availableRooms){
                    if(or.getClinicId() == unnaprovedApp.getClinic().getId()){
                        //ZAUZMI SOBU

                        OperationRoom operationRoom = operationRoomRepository.findById(or.getRoomId()).orElse(null);
                        if(operationRoom == null){
                            return;
                        }
                        unnaprovedApp.setOperationRoom(operationRoom);
                        appointmentRepository.save(unnaprovedApp);
                    }
                }
            }else {
                //AUTOMACKI ZAUZMI SOBE ZA OPERACIJE
            }
        }
    }

    @Override
    public AppointmentDTO changeDoctorAndAddRoom(ChangeDoctorRequestDTO changeDoctorRequestDTO) {

        Appointment appointment = appointmentRepository.findById(changeDoctorRequestDTO.getAppId()).orElseThrow(NotExistsException::new);
        OperationRoom operationRoom = operationRoomRepository.findById(changeDoctorRequestDTO.getRoomId()).orElseThrow(NotExistsException::new);
        Doctor doctor = doctorRepository.findById(changeDoctorRequestDTO.getDoctorId()).orElseThrow(NotExistsException::new);

        if(appointment instanceof Examination){
            Examination ex = (Examination) appointment;
            List<Doctor> doctors = doctorRepository.findAllByExaminations(ex);
            if(!doctors.isEmpty() || doctors != null){
                Doctor currentDoctor = doctors.get(0);
                currentDoctor.getExaminations().remove(ex);
            }else {
                throw new NotExistsException();
            }

            doctor.getExaminations().add(ex);
            doctorRepository.save(doctor);
            appointment.setOperationRoom(operationRoom);
            appointmentRepository.save(appointment);
            return new AppointmentDTO(appointment);

        }

        return null;
    }

    // tries to schedule an appointment for selected date and time and doctor if he is available
    @Override
    public void reserveExaminationForPatient(Long selected_date, int hours, int minutes, Long doc_id, Long service_id) {
        Long millis_in_hour = 3600000L;
        Long millis_in_min = 60000L;
        Long duration = 1800000L;
        Long my_date = selected_date + (hours * millis_in_hour) + (minutes * millis_in_min);

        //DATUMI PODESENI

        com.proj.medicalClinic.model.Service my_service = serviceRepository.findById(service_id).orElseThrow(NotExistsException::new);
        Doctor my_doc = doctorRepository.findById(doc_id).orElseThrow(NotExistsException::new);
        // check shifts
//        if(my_doc.getShift() == 1){
//            if(hours >= 8){
//                throw new NotExistsException();
//            }
//        }
//        else if(my_doc.getShift() == 2){
//            if(hours < 8 || hours >= 16){
//                throw new NotExistsException();
//            }
//        }
//        else{
//            if(hours < 16){
//                throw new NotExistsException();
//            }
//        }


        List<Operation> operations = operationRepository.findAllByDoctorsContaining(my_doc);
        List<Examination> examinations = examinationRepository.findAllByDoctorsContaining(my_doc);
        List<Leave> leaves = leaveRepository.findAllByDoctor(my_doc);

        for(Operation o : operations){
            // ako pocetak upada izmedju neko app
            if((o.getDate().getTime() < my_date) && ((o.getDate().getTime() + o.getDuration() * millis_in_min) > my_date)){
                System.out.println("UDJE JEDAN");
                throw new NotExistsException();
            }
            // ako kraj upada izmedju nekog app
            if((o.getDate().getTime() < (my_date + 30 * millis_in_min)) && ((o.getDate().getTime() + o.getDuration() * millis_in_min) > (my_date + 30 * millis_in_min))){
                System.out.println("UDJE DVA");
                throw new NotExistsException();
            }
        }

        for(Examination o : examinations){
            if((o.getDate().getTime() < my_date) && ((o.getDate().getTime() + o.getDuration() * millis_in_min) > my_date)){
                System.out.println("UDJE TRI");
                throw new NotExistsException();
            }
            if((o.getDate().getTime() < (my_date + 30 * millis_in_min)) && ((o.getDate().getTime() + o.getDuration() * millis_in_min) > (my_date + 30 * millis_in_min))){
                System.out.println("UDJE CETIRI");
                throw new NotExistsException();
            }
        }

        for(Leave l : leaves){
            if((l.getDateStart().getTime() < my_date) && (l.getDateEnd().getTime() > my_date)){
                System.out.println("UDJE PET");
                throw new NotExistsException();
            }
            if((l.getDateStart().getTime() < (my_date + 30 * millis_in_min)) && (l.getDateEnd().getTime() > (my_date + 30 * millis_in_min))){
                System.out.println("UDJE SEST");
                throw new NotExistsException();
            }
        }

        Examination ex = new Examination();
        List<Doctor> list_doc = new ArrayList<Doctor>();
        list_doc.add(my_doc);
        ex.setDoctors(list_doc);
        ex.setFast(false);
        ex.setHeld(false);
        ex.setNurse(null);
        ex.setOperationRoom(null);
        ex.setDate(new Date(my_date));
        ex.setDuration(30);
        // kako preuzeti pacijenta
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        System.out.println(username);
        Patient p =(Patient) appUserRepository.findByEmail(username).orElseThrow(NotExistsException::new);

        ex.setPatient(p);
        ex.setClinic(my_doc.getClinic());
        ex.setService(my_service);

        examinationRepository.save(ex);

        List<Examination> ex_for_doc = new ArrayList<>();
        ex_for_doc = my_doc.getExaminations();
        ex_for_doc.add(ex);
        my_doc.setExaminations(ex_for_doc);

        doctorRepository.save(my_doc);

        return;
    }


}
