package ra.edu.dto;

import ra.edu.utils.EEnrollmentStt;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EnrollmentDTO {
    private Integer id;
    private Integer studentId;
    private Integer courseId;
    private LocalDateTime registeredAt;
    private EEnrollmentStt status;
}
