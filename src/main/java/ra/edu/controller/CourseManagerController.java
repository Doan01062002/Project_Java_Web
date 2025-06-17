package ra.edu.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ra.edu.dto.CourseDTO;
import ra.edu.dto.StudentDTO;
import ra.edu.entity.Course;
import ra.edu.service.CourseService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 30
)
@RequestMapping("/course_manager")
public class CourseManagerController {

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private CourseService courseService;

    @GetMapping("/show")
    public String showCourseManager(
            @RequestParam(name = "confirm", required = false) Integer confirmId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
            @RequestParam(name = "add", required = false) String add,
            @RequestParam(name = "edit", required = false) Integer editId,
            HttpSession session, Model model) {

        StudentDTO loggedInUser = (StudentDTO) session.getAttribute("loggedInUser");
        if (loggedInUser == null || !Boolean.TRUE.equals(loggedInUser.getRole())) {
            return "redirect:/login_form";
        }

        if (confirmId != null) {
            Course confirmCourse = courseService.getCourseById(confirmId);
            model.addAttribute("showConfirmModal", true);
            model.addAttribute("confirmCourse", confirmCourse);
        }

        int pageSize = 5;
        List<Course> courses = courseService.searchAndSortCourses(keyword, sortBy, sortDir, page, pageSize);
        long totalCourses = courseService.countSearchedCourses(keyword);
        int totalPages = (int) Math.ceil((double) totalCourses / pageSize);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("listCourse", courses);
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);

        // Modal code giữ nguyên như cũ
        if (add != null) {
            model.addAttribute("showAddModal", true);
            model.addAttribute("courseDTO", new CourseDTO());
        }
        if (editId != null) {
            Course course = courseService.getCourseById(editId);
            CourseDTO courseDTO = new CourseDTO();
            courseDTO.setId(course.getId());
            courseDTO.setName(course.getName());
            courseDTO.setDuration(course.getDuration());
            courseDTO.setInstructor(course.getInstructor());
            courseDTO.setCreateAt(course.getCreateAt());
            courseDTO.setImage(course.getImage());
            model.addAttribute("showEditModal", true);
            model.addAttribute("courseDTO", courseDTO);
        }

        return "course_manager";
    }

    @PostMapping("/save")
    public String saveAddCourse(@Valid @ModelAttribute("courseDTO") CourseDTO courseDTO,
                                BindingResult bindingResult,
                                @RequestParam(name = "page", defaultValue = "1") int page,
                                Model model) {
        if (bindingResult.hasErrors()) {
            // Bổ sung lại dữ liệu phân trang cho trang course_manager
            int pageSize = 5;
            long totalCourses = courseService.countTotalCourses();
            int totalPages = (int) Math.ceil((double) totalCourses / pageSize);

            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("listCourse", courseService.findAllByPage(page, pageSize));
            model.addAttribute("showAddModal", true);
            return "course_manager";
        }


        if (courseService.isCourseNameDuplicate(courseDTO.getName())) {
            bindingResult.rejectValue("name", "error.courseDTO", "Tên khoá học đã tồn tại");

            // Phục hồi dữ liệu phân trang để quay về course_manager
            int pageSize = 5;
            long totalCourses = courseService.countTotalCourses();
            int totalPages = (int) Math.ceil((double) totalCourses / pageSize);

            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("listCourse", courseService.findAllByPage(page, pageSize));
            model.addAttribute("showAddModal", true);

            return "course_manager";
        }


        MultipartFile fileImage = courseDTO.getImageFile();
        try {
            if (fileImage != null && !fileImage.isEmpty()) {
                Map uploadResult = cloudinary.uploader().upload(fileImage.getBytes(), ObjectUtils.emptyMap());
                String url = uploadResult.get("url").toString();
                courseDTO.setImage(url);
            }
            courseService.addOrUpdateCourse(courseDTO);
        } catch (IOException exception) {
            model.addAttribute("error", "Lỗi upload ảnh: " + exception.getMessage());
            model.addAttribute("showAddModal", true);
            return "course_manager";
        }

        return "redirect:/course_manager/show";
    }

    @PostMapping("/save_edit_course")
    public String saveEditCourse(@Valid @ModelAttribute("courseDTO") CourseDTO courseDTO,
                                 BindingResult bindingResult,
                                 @RequestParam(name = "page", defaultValue = "1") int page,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            int pageSize = 5;
            long totalCourses = courseService.countTotalCourses();
            int totalPages = (int) Math.ceil((double) totalCourses / pageSize);

            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("listCourse", courseService.findAllByPage(page, pageSize));
            model.addAttribute("showEditModal", true);
            return "course_manager";
        }

        if (courseService.isCourseNameDuplicate(courseDTO.getName(), courseDTO.getId())) {
            bindingResult.rejectValue("name", "error.courseDTO", "Tên khoá học đã tồn tại");

            // Phục hồi dữ liệu
            int pageSize = 5;
            long totalCourses = courseService.countTotalCourses();
            int totalPages = (int) Math.ceil((double) totalCourses / pageSize);

            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("listCourse", courseService.findAllByPage(page, pageSize));
            model.addAttribute("showEditModal", true);

            return "course_manager";
        }


        MultipartFile fileImage = courseDTO.getImageFile();
        try {
            if (fileImage != null && !fileImage.isEmpty()) {
                Map uploadResult = cloudinary.uploader().upload(fileImage.getBytes(), ObjectUtils.emptyMap());
                String url = uploadResult.get("url").toString();
                courseDTO.setImage(url);
            }
            courseService.addOrUpdateCourse(courseDTO);
        } catch (IOException exception) {
            model.addAttribute("error", "Lỗi upload ảnh: " + exception.getMessage());
            model.addAttribute("showEditModal", true);
            return "course_manager";
        }

        return "redirect:/course_manager/show";
    }

    @GetMapping("/delete_course/{id}")
    public String deleteCourse(@PathVariable("id") int id) {
        courseService.deleteCourse(courseService.getCourseById(id));
        return "redirect:/course_manager/show";
    }
}