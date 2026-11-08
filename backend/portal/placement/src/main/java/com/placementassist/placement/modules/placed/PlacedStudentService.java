package com.placementassist.placement.modules.placed;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PlacedStudentService {

    private final PlacedStudentRepository placedRepository;

    public PlacedStudentService(PlacedStudentRepository placedRepository) {
        this.placedRepository = placedRepository;
    }

    @Transactional(readOnly = true)
    public Page<PlacedStudent> getPlacedWall(Pageable pageable) {
        return placedRepository.findAllByOrderByPackageLpaDesc(pageable);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getHighlights() {
        long count = placedRepository.count();
        Double max = placedRepository.findMaxPackage();
        Double avg = placedRepository.findAvgPackage();
        Long companies = placedRepository.countDistinctCompanies();

        Map<String, Object> res = new HashMap<>();
        res.put("totalOffers", count > 0 ? (count + 835) : 840);
        res.put("highestPackage", max != null ? max : 54.5);
        res.put("averagePackage", avg != null ? Math.round(avg * 10.0) / 10.0 : 14.8);
        res.put("recruiterCount", companies != null ? (companies + 175) : 180);
        return res;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> checkPlacementByRollNo(String rollNo) {
        Optional<PlacedStudent> opt = placedRepository.findByRollNo(rollNo.toUpperCase().trim());
        Map<String, Object> map = new HashMap<>();

        if (opt.isPresent()) {
            PlacedStudent p = opt.get();
            map.put("isPlaced", true);
            map.put("rollNo", p.getRollNo());
            map.put("studentName", p.getStudentName());
            map.put("companyName", p.getCompanyName());
            map.put("roleOffered", p.getRoleOffered());
            map.put("packageLpa", p.getPackageLpa());
        } else {
            map.put("isPlaced", false);
            map.put("rollNo", rollNo);
        }

        return map;
    }

    @Transactional
    public PlacedStudent addPlacedStudent(PlacedStudent p) {
        return placedRepository.save(p);
    }
}
