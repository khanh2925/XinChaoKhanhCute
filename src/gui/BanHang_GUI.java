package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;

import customcomponent.TaoJtextNhanh;
import customcomponent.TaoLabelNhanh;
import dao.ChiTietBangGia_DAO;
import dao.ChiTietKhuyenMaiSanPham_DAO;
import dao.LoSanPham_DAO;
import dao.QuyCachDongGoi_DAO;
import dao.SanPham_DAO;
import entity.ChiTietBangGia;
import entity.ChiTietKhuyenMaiSanPham;
import entity.KhuyenMai;
import entity.LoSanPham;
import entity.QuyCachDongGoi;
import entity.SanPham;
import customcomponent.TaoButtonNhanh;

/**
 * Giao diện Bán Hàng - KHUNG LAYOUT TRỐNG HOÀN TOÀN
 * Dùng class tiện ích: TaoJtextNhanh, TaoLabelNhanh, TaoButtonNhanh
 */
public class BanHang_GUI extends JPanel implements ActionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txtTimThuoc;
    private JPanel pnDanhSachDon;
    private JTextField txtTimKH;
    private JTextField txtTienKhach;
    private JTextField txtTongTienHang;
    private JTextField txtTongHDValue;
    private JTextField txtTienThua;
    private JTextField txtTenKhachHang;
    private JButton btnThemDon;
    private JButton btnBanHang;
    private JTextField txtGiamSPValue;
    private JTextField txtGiamHDValue;
    private SanPham_DAO sanPhamDao;
    private LoSanPham_DAO loSanPhamDao;
    private QuyCachDongGoi_DAO quyCachDongGoiDao;
    private ChiTietKhuyenMaiSanPham_DAO ctKMSPDao;
    private ChiTietBangGia_DAO ctBGDao;

    public BanHang_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        initialize();
        sanPhamDao = new SanPham_DAO();
        loSanPhamDao = new LoSanPham_DAO();
        quyCachDongGoiDao = new QuyCachDongGoi_DAO();
        ctKMSPDao= new ChiTietKhuyenMaiSanPham_DAO();
        ctBGDao = new ChiTietBangGia_DAO();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createRightPanel(), BorderLayout.EAST);
    }

    private JPanel createHeaderPanel() {
        JPanel pnHeader = new JPanel(null);
        pnHeader.setPreferredSize(new Dimension(1073, 88));
        pnHeader.setBackground(new Color(0xE3F2F5));
        txtTimThuoc = TaoJtextNhanh.timKiem();
        txtTimThuoc.setBounds(25, 17, 480, 60);
        txtTimThuoc.addActionListener(this);
        pnHeader.add(txtTimThuoc);
        btnThemDon = new JButton("Thêm đơn");
        btnThemDon.setBounds(530, 30, 130, 45);
        pnHeader.add(btnThemDon);
        return pnHeader;
    }

    private JPanel createCenterPanel() {
        JPanel pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new CompoundBorder(
                new LineBorder(new Color(0x00C853), 3, true),
                new EmptyBorder(10, 10, 10, 10)));

        pnDanhSachDon = new JPanel();
        pnDanhSachDon.setLayout(new BoxLayout(pnDanhSachDon, BoxLayout.Y_AXIS));
        pnDanhSachDon.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(pnDanhSachDon);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        pnCenter.add(scrollPane, BorderLayout.CENTER);

        return pnCenter;
    }

		   private void themSanPham(
		        int stt,
		        String tenThuoc,
		        String maLo,           // lô đang bán
		        int tonKho,           // tồn kho của lô đó (theo đơn vị gốc)
		        String[] donViArr,    // danh sách đơn vị
		        int[] heSoArr,
		        double[] giaArr,
		        int soLuong,
		        ChiTietKhuyenMaiSanPham kmSP,
		        String anhPath
		) {
		    final int tonQuyVeDonViNhoNhat = tonKho * heSoArr[0];
		    final Box[] rowTangThem = {null};
		    Box row = Box.createHorizontalBox();
		    row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
		    row.setBorder(new CompoundBorder(
		            new LineBorder(new Color(0xDDDDDD), 1),
		            new EmptyBorder(8, 10, 8, 10)
		    ));
		    row.setBackground(new Color(0xFAFAFA));
		    row.setOpaque(true);
		
		    // STT
		    JLabel lblSTT = new JLabel(String.valueOf(stt));
		    lblSTT.setFont(new Font("Segoe UI", Font.BOLD, 16));
		    lblSTT.setPreferredSize(new Dimension(40, 30));
		    row.add(lblSTT);
		    row.add(Box.createHorizontalStrut(10));
		
		    // Hình ảnh
		    JLabel lblAnh = new JLabel();
		    lblAnh.setPreferredSize(new Dimension(80, 80));
		    lblAnh.setBorder(new LineBorder(Color.LIGHT_GRAY));
		    try {
		        ImageIcon icon = new ImageIcon(getClass().getResource(anhPath));
		        lblAnh.setIcon(new ImageIcon(icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
		    } catch (Exception e) {
		        lblAnh.setText("Ảnh");
		    }
		    row.add(lblAnh);
		    row.add(Box.createHorizontalStrut(10));
		
		    // === THÔNG TIN THUỐC ===
		    Box infoBox = Box.createVerticalBox();
		    infoBox.setBorder(new LineBorder(Color.BLACK));
		    JTextField txtTenThuoc = TaoJtextNhanh.taoTextDonHang(
		            tenThuoc, new Font("Segoe UI", Font.BOLD, 16), new Color(0x00796B), 300);
		    infoBox.add(txtTenThuoc);
		
		    // Lô + tồn
		    Box loBox = Box.createHorizontalBox();
		    loBox.setMaximumSize(new Dimension(300, 30));
		
		    JTextField txtLo = TaoJtextNhanh.taoTextDonHang(
		            "Lô: " + maLo, new Font("Segoe UI", Font.BOLD, 16), new Color(0x00796B), 150);
		    loBox.add(txtLo);
		    loBox.add(Box.createHorizontalStrut(8));
		
		    // dùng biến local final để cập nhật được trong lambda
		    final JTextField txtTonLocal = TaoJtextNhanh.taoTextDonHang(
		            "Tồn: " + tonKho, new Font("Segoe UI", Font.BOLD, 16), new Color(0x00796B), 150);
		    loBox.add(txtTonLocal);
		
		    infoBox.add(loBox);
		    row.add(infoBox);
		    row.add(Box.createHorizontalStrut(10));
		
		    // === ĐƠN VỊ TÍNH ===
		    JComboBox<String> cbDonVi = new JComboBox<>(donViArr);
		    cbDonVi.setPreferredSize(new Dimension(70, 30));
		    cbDonVi.setMaximumSize(new Dimension(70, 30));
		    cbDonVi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		    cbDonVi.setSelectedIndex(0);
		    row.add(cbDonVi);
		    
			JLabel lblQuyDoi = new JLabel();
			lblQuyDoi.setFont(new Font("Segoe UI", Font.PLAIN, 12));
			lblQuyDoi.setPreferredSize(new Dimension(80, 30)); // tuỳ chỉnh
			row.add(Box.createHorizontalStrut(5));
			row.add(lblQuyDoi);
	
		    row.add(Box.createHorizontalStrut(20));
		
		    // === SỐ LƯỢNG + / - ===
		    Box soLuongBox = Box.createHorizontalBox();
		    soLuongBox.setMaximumSize(new Dimension(140, 30));
		    soLuongBox.setPreferredSize(new Dimension(140, 30));
		    soLuongBox.setBorder(new LineBorder(new Color(0xDDDDDD), 1, true));
		
		    JButton btnGiam = new JButton("-");
		    btnGiam.setFont(new Font("Segoe UI", Font.BOLD, 16));
		    btnGiam.setPreferredSize(new Dimension(40, 30));
		    btnGiam.setMargin(new Insets(0, 0, 0, 0));
		    btnGiam.setFocusPainted(false);
		    soLuongBox.add(btnGiam);
		
		    JTextField txtSoLuong = TaoJtextNhanh.hienThi(
		            String.valueOf(soLuong), new Font("Segoe UI", Font.PLAIN, 16), Color.BLACK);
		    txtSoLuong.setMaximumSize(new Dimension(600, 30));
		    txtSoLuong.setHorizontalAlignment(SwingConstants.CENTER);
		    txtSoLuong.setEditable(true);
		    soLuongBox.add(txtSoLuong);
		
		    JButton btnTang = new JButton("+");
		    btnTang.setFont(new Font("Segoe UI", Font.BOLD, 16));
		    btnTang.setPreferredSize(new Dimension(40, 30));
		    btnTang.setMargin(new Insets(0, 0, 0, 0));
		    btnTang.setFocusPainted(false);
		    soLuongBox.add(btnTang);
		
		    row.add(soLuongBox);
		    row.add(Box.createHorizontalStrut(10));
		
		    // === KHUYẾN MÃI ===
		    String tenKM = (kmSP != null)
		            ? kmSP.getKhuyenMai().getTenKM()
		            : "Không có KM";
		    JTextField txtKM = TaoJtextNhanh.taoTextDonHang(
		            tenKM, new Font("Segoe UI", Font.PLAIN, 16), Color.BLACK, 110
		    );
		    row.add(txtKM);
		    row.add(Box.createHorizontalStrut(10));
		
		    // === ĐƠN GIÁ ===
		    double donGiaBanDau = giaArr[0];
		    String donGiaStr = formatTien(donGiaBanDau);
		    JTextField txtDonGia = TaoJtextNhanh.taoTextDonHang(
		            donGiaStr, new Font("Segoe UI", Font.PLAIN, 16), Color.BLACK, 100);
		    txtDonGia.setHorizontalAlignment(SwingConstants.RIGHT);
		    row.add(txtDonGia);
		    row.add(Box.createHorizontalStrut(10));
		
		    // === THÀNH TIỀN ===
		    double thanhTienBanDau = donGiaBanDau * soLuong;
		    JTextField txtThanhTien = TaoJtextNhanh.taoTextDonHang(
		            formatTien(thanhTienBanDau), new Font("Segoe UI", Font.BOLD, 16), new Color(0xD32F2F), 120);
		    txtThanhTien.setHorizontalAlignment(SwingConstants.RIGHT);
		    row.add(txtThanhTien);
		    row.add(Box.createHorizontalGlue());
		
		    // === NÚT XÓA ===
		    JButton btnXoa = new JButton();
		    btnXoa.setPreferredSize(new Dimension(40, 40));
		    btnXoa.setBorderPainted(false);
		    btnXoa.setContentAreaFilled(false);
		    btnXoa.setCursor(new Cursor(Cursor.HAND_CURSOR));
		    try {
		        ImageIcon icon = new ImageIcon(getClass().getResource("/images/bin.png"));
		        btnXoa.setIcon(new ImageIcon(icon.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH)));
		    } catch (Exception ignored) {}
		    row.add(btnXoa);
		
		    // ================= HÀM CẬP NHẬT CHUNG =================
		    final boolean[] daTangThem = {false};
		    Runnable capNhatDong = () -> {
		        int index = cbDonVi.getSelectedIndex();
		        if (index < 0) return;
		
		        int heSo = heSoArr[index];
		        double giaHienTai = giaArr[index];
		        double donGiaSauKm = giaHienTai;
		        int heSoSoVoiDonViNhoNhat = heSoArr[index];
		        lblQuyDoi.setText("x" + heSoSoVoiDonViNhoNhat + " " + donViArr[0]);
		
		        int sl;
		        try {
		            sl = Integer.parseInt(txtSoLuong.getText().trim());
		        } catch (NumberFormatException ex) {
		            sl = 0;
		        }
		
		        if (sl < 0) sl = 0;
		
		        int slToiDa = tonQuyVeDonViNhoNhat / heSo;
		        int slCheck = sl;
		        
		        if(kmSP != null && kmSP.getKhuyenMai().getHinhThuc().name().equals("TANG_THEM")&& daTangThem[0]) {
		        	slCheck = sl + kmSP.getSoLuongTangThem();
		        }
		        
		        if (slCheck > slToiDa) {
		            JOptionPane.showMessageDialog(
		                    this,
		                    "Số lượng vượt quá tồn kho!(đã tính cả hàng tặng)!",
		                    "Cảnh báo",
		                    JOptionPane.WARNING_MESSAGE
		            );
		            sl = slToiDa - kmSP.getSoLuongTangThem();
		            txtSoLuong.setText(String.valueOf(sl));
		        }
		
		        int tonMoi = tonQuyVeDonViNhoNhat / heSo;

		
		        txtDonGia.setText(formatTien(donGiaSauKm));
		        double thanhTienMoi = donGiaSauKm * sl;
		        txtThanhTien.setText(formatTien(thanhTienMoi));
		
		        // ===== XỬ LÝ KHUYẾN MÃI TẶNG THÊM // GIẢM GIÁ=====
		        if(kmSP != null ) {
		        	String hinhThuc = kmSP.getKhuyenMai().getHinhThuc().name();
		        	// nếu theo phần trăm
		        	if (hinhThuc.equals("GIAM_GIA_PHAN_TRAM")) {
		                double phanTram = kmSP.getKhuyenMai().getGiaTri();
		                if (sl >= kmSP.getSoLuongToiThieu()) {
		                    donGiaSauKm = giaHienTai * (1 - phanTram / 100.0);
		                    if (donGiaSauKm < 0) donGiaSauKm = 0;

		                    txtKM.setText("Giảm " + (int) phanTram + "%");
		                    txtKM.setToolTipText("Mua " + (int) kmSP.getSoLuongToiThieu()
		                            + " giảm " + (int) kmSP.getKhuyenMai().getGiaTri() + "%");

		                    // Tooltip cho THÀNH TIỀN (giá gốc / giảm / sau giảm)
		                    double giam = giaHienTai - donGiaSauKm;
		                    txtThanhTien.setToolTipText(
		                            "<html>"
		                                    + "Giá gốc: " + formatTien(giaHienTai) + "/đv<br>"
		                                    + "Giảm: " + formatTien(giam) + "/đv (" + (int) phanTram + "%)<br>"
		                                    + "Giá sau giảm: " + formatTien(donGiaSauKm) + "/đv"
		                                    + "</html>"
		                    );
		                } else {
		                    txtKM.setText("Mua ≥ " + kmSP.getSoLuongToiThieu()
		                            + " giảm " + (int) phanTram + "%");
		                    // chưa đủ điều kiện: không cần tooltip
		                }
		            }

		        	// xử lý theo số tiền
		        	if (hinhThuc.equals("GIAM_GIA_TIEN")) {
		                double soTienGiam = kmSP.getKhuyenMai().getGiaTri();
		                if (sl >= kmSP.getSoLuongToiThieu()) {
		                    donGiaSauKm = giaHienTai - soTienGiam;
		                    if (donGiaSauKm < 0) donGiaSauKm = 0;

		                    txtKM.setText("Giảm " + formatTien(soTienGiam) + "/đv (≥ "
		                            + kmSP.getSoLuongToiThieu() + ")");

		                    txtKM.setToolTipText("Mua " + (int) kmSP.getSoLuongToiThieu()
		                            + " giảm " + formatTien(soTienGiam) + "/đv");

		                    // Tooltip thành tiền
		                    txtThanhTien.setToolTipText(
		                            "<html>"
		                                    + "Giá gốc: " + formatTien(giaHienTai) + "/đv<br>"
		                                    + "Giảm: " + formatTien(soTienGiam) + "/đv<br>"
		                                    + "Giá sau giảm: " + formatTien(donGiaSauKm) + "/đv"
		                                    + "</html>"
		                    );
		                } else {
		                    txtKM.setText("Mua ≥ " + kmSP.getSoLuongToiThieu()
		                            + " giảm " + formatTien(soTienGiam) + "/đv");
		                    // chưa đủ điều kiện: không cần tooltip
		                }
		            }

		        	if (hinhThuc.equals("TANG_THEM")) {
		        	    int slQuyVeNhoNhat = sl * heSo;

		        	    if (slQuyVeNhoNhat >= kmSP.getSoLuongToiThieu()) {
		        	        txtKM.setText("Tặng thêm " + kmSP.getSoLuongTangThem() + " " + donViArr[0]);
		        	        txtKM.setToolTipText(
		        	            "Mua ≥ " + kmSP.getSoLuongToiThieu()
		        	            + " tặng thêm " + kmSP.getSoLuongTangThem() + " " + donViArr[0]
		        	        );

		        	        if (!daTangThem[0]) {
		        	            rowTangThem[0] = taoRowTangThem(
		        	                    tenThuoc, maLo, tonMoi, donViArr[0],
		        	                    kmSP.getSoLuongTangThem(), anhPath
		        	            );
		        	            daTangThem[0] = true;
		        	        }
		        	    } else {
		        	        // chỉ hiện điều kiện, không tooltip
		        	        txtKM.setText(
		        	            "Mua ≥ " + kmSP.getSoLuongToiThieu()
		        	            + " tặng thêm " + kmSP.getSoLuongTangThem() + " " + donViArr[0]
		        	        );
		        	        txtKM.setToolTipText(null);

		        	        if (daTangThem[0] && rowTangThem[0] != null) {
		        	            pnDanhSachDon.remove(rowTangThem[0]);
		        	            pnDanhSachDon.revalidate();
		        	            pnDanhSachDon.repaint();
		        	            rowTangThem[0] = null;
		        	        }
		        	        daTangThem[0] = false;
		        	    }
		        	}

		        }
		        
		        
		    };
		
		    // =============== GẮN CÁC SỰ KIỆN ===============
		
		    // Đổi đơn vị
		    cbDonVi.addActionListener(ev -> capNhatDong.run());
		
		    // Tăng
		    btnTang.addActionListener(ev -> {
		        int sl;
		        try {
		            sl = Integer.parseInt(txtSoLuong.getText().trim());
		        } catch (NumberFormatException ex) {
		            sl = 0;
		        }
		        sl++;
		        txtSoLuong.setText(String.valueOf(sl));
		        capNhatDong.run();
		    });
		
		    // Giảm
		    btnGiam.addActionListener(ev -> {
		        int sl;
		        try {
		            sl = Integer.parseInt(txtSoLuong.getText().trim());
		        } catch (NumberFormatException ex) {
		            sl = 1;
		        }
		        sl--;
		        if (sl < 1) {
		            JOptionPane.showMessageDialog(this, "Số lượng không được bé hơn 1");
		            return;
		        }
		        txtSoLuong.setText(String.valueOf(sl));
		        capNhatDong.run();
		    });
		
		    // Nhập tay + Enter
		    txtSoLuong.addActionListener(ev -> capNhatDong.run());
		
		    // Mất focus cũng cập nhật
		    txtSoLuong.addFocusListener(new FocusAdapter() {
		        @Override
		        public void focusLost(FocusEvent e) {
		            capNhatDong.run();
		        }
		    });
		
		    // Cập nhật 1 lần ban đầu
		    capNhatDong.run();
		
		    // Thêm vào danh sách
		    pnDanhSachDon.add(row);
		    pnDanhSachDon.add(Box.createVerticalStrut(5));
		}

		   private Box taoRowTangThem(String tenSP, String maLo, int tonHienTai,
                   String donViNhoNhat, int slTang, String anhPath) {

				Box row = Box.createHorizontalBox();
				row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
				row.setBorder(new CompoundBorder(
				   new LineBorder(new Color(0xDDDDDD), 1),
				   new EmptyBorder(8, 10, 8, 10)
				));
				row.setBackground(new Color(0xFAFAFA));
				row.setOpaque(true);
				
				// STT ghi "Tặng"
				JLabel lblSTT = new JLabel("Tặng");
				lblSTT.setFont(new Font("Segoe UI", Font.BOLD, 16));
				lblSTT.setPreferredSize(new Dimension(40, 30));
				row.add(lblSTT);
				row.add(Box.createHorizontalStrut(10));
				
				// Ảnh (giống dòng gốc)
				JLabel lblAnh = new JLabel();
				lblAnh.setPreferredSize(new Dimension(80, 80));
				lblAnh.setBorder(new LineBorder(Color.LIGHT_GRAY));
				try {
				ImageIcon icon = new ImageIcon(getClass().getResource(anhPath));
				lblAnh.setIcon(new ImageIcon(icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
				} catch (Exception e) {
				lblAnh.setText("Ảnh");
				}
				row.add(lblAnh);
				row.add(Box.createHorizontalStrut(10));
				
				// ===== THÔNG TIN THUỐC (giống dòng gốc) =====
				Box infoBox = Box.createVerticalBox();
				infoBox.setBorder(new LineBorder(Color.BLACK));
				
				// tên + thêm chữ (Hàng tặng)
				JTextField txtTenThuoc = TaoJtextNhanh.taoTextDonHang(
				   tenSP + " (Hàng tặng)", new Font("Segoe UI", Font.BOLD, 16), new Color(0x00796B), 300);
				infoBox.add(txtTenThuoc);
				
				// Lô + tồn
				Box loBox = Box.createHorizontalBox();
				loBox.setMaximumSize(new Dimension(300, 30));
				
				JTextField txtLo = TaoJtextNhanh.taoTextDonHang(
				   "Lô: " + maLo, new Font("Segoe UI", Font.BOLD, 16), new Color(0x00796B), 150);
				loBox.add(txtLo);
				loBox.add(Box.createHorizontalStrut(8));
				
				JTextField txtTon = TaoJtextNhanh.taoTextDonHang(
				   "Tồn: " + tonHienTai, new Font("Segoe UI", Font.BOLD, 16), new Color(0x00796B), 150);
				loBox.add(txtTon);
				
				infoBox.add(loBox);
				row.add(infoBox);
				row.add(Box.createHorizontalStrut(10));
				
				// ===== ĐƠN VỊ TÍNH (giống gốc nhưng KHÓA) =====
				JComboBox<String> cbDonVi = new JComboBox<>(new String[]{donViNhoNhat});
				cbDonVi.setPreferredSize(new Dimension(70, 30));
				cbDonVi.setMaximumSize(new Dimension(70, 30));
				cbDonVi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
				cbDonVi.setSelectedIndex(0);
				cbDonVi.setEnabled(false);  // không cho đổi
				row.add(cbDonVi);
				
				row.add(Box.createHorizontalStrut(20));
				
				// ===== SỐ LƯỢNG (giống gốc nhưng KHÔNG cho sửa) =====
				Box soLuongBox = Box.createHorizontalBox();
				soLuongBox.setMaximumSize(new Dimension(140, 30));
				soLuongBox.setPreferredSize(new Dimension(140, 30));
				soLuongBox.setBorder(new LineBorder(new Color(0xDDDDDD), 1, true));
				
				JButton btnGiam = new JButton("-");
				btnGiam.setFont(new Font("Segoe UI", Font.BOLD, 16));
				btnGiam.setPreferredSize(new Dimension(40, 30));
				btnGiam.setMargin(new Insets(0, 0, 0, 0));
				btnGiam.setFocusPainted(false);
				btnGiam.setEnabled(false); // khóa nút
				soLuongBox.add(btnGiam);
				
				JTextField txtSL = TaoJtextNhanh.hienThi(
				   String.valueOf(slTang), new Font("Segoe UI", Font.PLAIN, 16), Color.BLACK);
				txtSL.setMaximumSize(new Dimension(600, 30));
				txtSL.setHorizontalAlignment(SwingConstants.CENTER);
				txtSL.setEditable(false); // không cho sửa
				soLuongBox.add(txtSL);
				
				JButton btnTang = new JButton("+");
				btnTang.setFont(new Font("Segoe UI", Font.BOLD, 16));
				btnTang.setPreferredSize(new Dimension(40, 30));
				btnTang.setMargin(new Insets(0, 0, 0, 0));
				btnTang.setFocusPainted(false);
				btnTang.setEnabled(false); // khóa nút
				soLuongBox.add(btnTang);
				
				row.add(soLuongBox);
				row.add(Box.createHorizontalStrut(10));
				
				// ===== KHUYẾN MÃI =====
				JTextField txtKM = TaoJtextNhanh.taoTextDonHang(
				   "Tặng thêm", new Font("Segoe UI", Font.PLAIN, 16), Color.BLACK, 110);
				txtKM.setEditable(false);
				row.add(txtKM);
				row.add(Box.createHorizontalStrut(10));
				
				// ===== ĐƠN GIÁ = 0 =====
				JTextField txtDonGia = TaoJtextNhanh.taoTextDonHang(
				   "0 đ", new Font("Segoe UI", Font.PLAIN, 16), Color.BLACK, 100);
				txtDonGia.setHorizontalAlignment(SwingConstants.RIGHT);
				txtDonGia.setEditable(false);
				row.add(txtDonGia);
				row.add(Box.createHorizontalStrut(10));
				
				// ===== THÀNH TIỀN = 0 =====
				JTextField txtThanhTien = TaoJtextNhanh.taoTextDonHang(
				   "0 đ", new Font("Segoe UI", Font.BOLD, 16), new Color(0xD32F2F), 120);
				txtThanhTien.setHorizontalAlignment(SwingConstants.RIGHT);
				txtThanhTien.setEditable(false);
				row.add(txtThanhTien);
				row.add(Box.createHorizontalGlue());
				
				// ❌ KHÔNG có nút XÓA ở cuối
				
				pnDanhSachDon.add(row);
				pnDanhSachDon.add(Box.createVerticalStrut(5));
				pnDanhSachDon.revalidate();
				pnDanhSachDon.repaint();
				return row;
				}

		

    private String formatTien(double tien) {
        DecimalFormat df = new DecimalFormat("#,##0");
        return df.format(tien) + " đ";
    }

	private JPanel createRightPanel() {
        JPanel pnRight = new JPanel();
        pnRight.setPreferredSize(new Dimension(450, 1080));
        pnRight.setBackground(Color.WHITE);
        pnRight.setBorder(new EmptyBorder(25, 25, 25, 25));
        pnRight.setLayout(new BoxLayout(pnRight, BoxLayout.Y_AXIS));

        // === TÌM KHÁCH HÀNG ===
        Box boxTimKhachHang = Box.createHorizontalBox();
        txtTimKH = TaoJtextNhanh.nhapLieu("Nhập SĐT khách hàng");
        boxTimKhachHang.add(txtTimKH);
        pnRight.add(boxTimKhachHang);
        pnRight.add(Box.createVerticalStrut(10));

        // === TÊN KHÁCH HÀNG ===
        Box boxTenKhachHang = Box.createHorizontalBox();
        boxTenKhachHang.add(TaoLabelNhanh.tieuDe("Tên khách hàng:"));
        txtTenKhachHang = TaoJtextNhanh.hienThi("Vãng lai", new Font("Segoe UI", Font.BOLD, 20), new Color(0x00796B));
        boxTenKhachHang.add(txtTenKhachHang);
        pnRight.add(boxTenKhachHang);
        pnRight.add(Box.createVerticalStrut(10));

        // === TỔNG TIỀN HÀNG ===
        Box boxTongTienHang = Box.createHorizontalBox();
        boxTongTienHang.add(TaoLabelNhanh.tieuDe("Tổng tiền hàng:"));
        txtTongTienHang = TaoJtextNhanh.hienThi("0 đ", new Font("Segoe UI", Font.BOLD, 20), Color.BLACK);
        boxTongTienHang.add(txtTongTienHang);
        pnRight.add(boxTongTienHang);
        pnRight.add(Box.createVerticalStrut(10));

        // === GIẢM GIÁ SẢN PHẨM ===
        Box boxGiamSP = Box.createHorizontalBox();
        boxGiamSP.add(TaoLabelNhanh.tieuDe("Giảm giá sản phẩm:"));
        txtGiamSPValue = TaoJtextNhanh.hienThi("0 đ", new Font("Segoe UI", Font.BOLD, 20), Color.BLACK);
        boxGiamSP.add(txtGiamSPValue);
        pnRight.add(boxGiamSP);
        pnRight.add(Box.createVerticalStrut(10));

        // === GIẢM GIÁ HÓA ĐƠN ===
        Box boxGiamHD = Box.createHorizontalBox();
        boxGiamHD.add(TaoLabelNhanh.tieuDe("Giảm giá hóa đơn:"));
        txtGiamHDValue = TaoJtextNhanh.hienThi("0 đ", new Font("Segoe UI", Font.BOLD, 20), Color.BLACK);
        boxGiamHD.add(txtGiamHDValue);
        pnRight.add(boxGiamHD);
        pnRight.add(Box.createVerticalStrut(10));

        // === TỔNG HÓA ĐƠN ===
        Box boxTongHD = Box.createHorizontalBox();
        boxTongHD.add(TaoLabelNhanh.tieuDe("Tổng hóa đơn:"));
        txtTongHDValue = TaoJtextNhanh.hienThi("0 đ", new Font("Segoe UI", Font.BOLD, 20), new Color(0xD32F2F));
        boxTongHD.add(txtTongHDValue);
        pnRight.add(boxTongHD);
        pnRight.add(Box.createVerticalStrut(10));

        // === TIỀN KHÁCH ĐƯA ===
        Box boxTienKhach = Box.createHorizontalBox();
        txtTienKhach = TaoJtextNhanh.nhapLieu("Nhập tiền khách đưa");
        boxTienKhach.add(txtTienKhach);
        pnRight.add(boxTienKhach);
        pnRight.add(Box.createVerticalStrut(10));

        // === GỢI Ý TIỀN ===
        Box goiYTien = Box.createVerticalBox();
        Box row1 = Box.createHorizontalBox();
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        row1.setAlignmentX(Component.CENTER_ALIGNMENT);
        row1.add(TaoButtonNhanh.goiY("50k"));
        row1.add(Box.createHorizontalStrut(5));
        row1.add(TaoButtonNhanh.goiY("100k"));
        row1.add(Box.createHorizontalStrut(5));
        row1.add(TaoButtonNhanh.goiY("200k"));

        Box row2 = Box.createHorizontalBox();
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        row2.setAlignmentX(Component.CENTER_ALIGNMENT);
        row2.add(TaoButtonNhanh.goiY("300k"));
        row2.add(Box.createHorizontalStrut(5));
        row2.add(TaoButtonNhanh.goiY("500k"));
        row2.add(Box.createHorizontalStrut(5));
        row2.add(TaoButtonNhanh.goiY("1000k"));

        goiYTien.add(row1);
        goiYTien.add(Box.createVerticalStrut(5));
        goiYTien.add(row2);
        pnRight.add(goiYTien);
        pnRight.add(Box.createVerticalStrut(10));

        // === TIỀN THỪA ===
        Box boxTienThua = Box.createHorizontalBox();
        boxTienThua.add(TaoLabelNhanh.tieuDe("Tiền thừa:"));
        txtTienThua = TaoJtextNhanh.hienThi("0 đ", new Font("Segoe UI", Font.BOLD, 20), new Color(0x00796B));
        boxTienThua.add(txtTienThua);
        pnRight.add(boxTienThua);
        pnRight.add(Box.createVerticalStrut(10));

        // === NÚT BÁN HÀNG ===
        btnBanHang = TaoButtonNhanh.banHang();
        btnBanHang.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnRight.add(btnBanHang);

        return pnRight;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Bán Hàng - Dùng Class Tiện Ích");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(1600, 900);
            f.setLocationRelativeTo(null);
            f.setContentPane(new BanHang_GUI());
            f.setVisible(true);
        });
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == txtTimThuoc) {
			xuLyTimThuoc();
		}
		
	}

	private void xuLyTimThuoc() {
    String tuKhoa = txtTimThuoc.getText().trim();
    if (tuKhoa.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Vui lòng nhập số đăng ký hoặc mã sản phẩm!");
        return;
    }

    // 1. Tìm sản phẩm
    SanPham sp = sanPhamDao.timSanPhamTheoSoDangKy(tuKhoa);
    if (sp == null) sp = sanPhamDao.laySanPhamTheoMa(tuKhoa);
    if (sp == null) {
        JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm với SĐK/Mã: " + tuKhoa);
        return;
    }

    // 2. Lấy danh sách lô
    List<LoSanPham> dsLo = loSanPhamDao.layDanhSachLoTheoMaSanPham(sp.getMaSanPham());
    if (dsLo.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Sản phẩm này không còn lô nào đang tồn kho!", 
                                     "Lỗi tồn kho", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // === KIỂM TRA SẢN PHẨM ĐÃ CÓ CHƯA ===
    LoSanPham loDauTien = dsLo.get(0);
    Box dongDaTonTai = timDongSanPhamTheoMaLo(loDauTien.getMaLo());
    
    if (dongDaTonTai != null) {
        // ✅ ĐÃ CÓ -> TĂNG SỐ LƯỢNG
        tangSoLuongDongSanPham(dongDaTonTai);
        txtTimThuoc.setText("");
        txtTimThuoc.requestFocus();
        
        JOptionPane.showMessageDialog(this, 
            "Đã tăng số lượng sản phẩm: " + sp.getTenSanPham(), 
            "Thông báo", 
            JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    // === CHƯA CÓ -> THÊM MỚI ===
    if (loDauTien.getSoLuongTon() <= 0) {
        JOptionPane.showMessageDialog(this, "Lô gần hết hạn đã hết hàng!", 
                                     "Hết hàng", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // 3. Lấy quy cách
    List<QuyCachDongGoi> dsQuyCach = quyCachDongGoiDao.layDanhSachQuyCachTheoSanPham(sp.getMaSanPham());
    QuyCachDongGoi quyCachGoc = dsQuyCach.stream()
            .filter(QuyCachDongGoi::isDonViGoc)
            .findFirst()
            .orElse(null);
    if (quyCachGoc == null) {
        JOptionPane.showMessageDialog(this, "Sản phẩm chưa có quy cách gốc!", 
                                     "Lỗi cấu hình", JOptionPane.ERROR_MESSAGE);
        return;
    }

    String[] donViArr = new String[dsQuyCach.size()];
    double[] giaArr = new double[dsQuyCach.size()];
    int[] heSoArr = new int[dsQuyCach.size()];
    
    for (int i = 0; i < dsQuyCach.size(); i++) {
        QuyCachDongGoi qc = dsQuyCach.get(i);
        donViArr[i] = qc.getDonViTinh().getTenDonViTinh();
        double giaGoc = sp.getGiaBan() * qc.getHeSoQuyDoi();
        giaArr[i] = giaGoc - giaGoc * qc.getTiLeGiam();
        heSoArr[i] = qc.getHeSoQuyDoi();
    }

    // 4. Lấy khuyến mãi
    List<ChiTietKhuyenMaiSanPham> dsKMSP = ctKMSPDao.layChiTietKhuyenMaiDangHoatDongTheoMaSP(sp.getMaSanPham());
    ChiTietKhuyenMaiSanPham kmSP = dsKMSP.isEmpty() ? null : dsKMSP.get(0);

    // 5. Ảnh
    String anhPath = sp.getHinhAnh();
    if (anhPath == null || anhPath.isEmpty()) {
        anhPath = "/images/default_medicine.png";
    }

    int stt = pnDanhSachDon.getComponentCount() / 2 + 1;

    themSanPham(
        stt, sp.getTenSanPham(), loDauTien.getMaLo(),
        loDauTien.getSoLuongTon(), donViArr, heSoArr,
        giaArr, 1, kmSP, anhPath
    );

    pnDanhSachDon.revalidate();
    pnDanhSachDon.repaint();
    txtTimThuoc.setText("");
    txtTimThuoc.requestFocus();
}
	private Box timDongSanPhamTheoMaLo(String maLo) {
	    Component[] components = pnDanhSachDon.getComponents();
	    
	    for (Component comp : components) {
	        if (comp instanceof Box) {
	            Box row = (Box) comp;
	            // Duyệt qua các component trong Box để tìm TextField chứa mã lô
	            for (Component inner : row.getComponents()) {
	                if (inner instanceof Box) {
	                    Box infoBox = (Box) inner;
	                    for (Component field : infoBox.getComponents()) {
	                        if (field instanceof Box) {
	                            Box loBox = (Box) field;
	                            for (Component txtField : loBox.getComponents()) {
	                                if (txtField instanceof JTextField) {
	                                    JTextField txt = (JTextField) txtField;
	                                    String text = txt.getText();
	                                    if (text.startsWith("Lô: ") && text.contains(maLo)) {
	                                        return row;
	                                    }
	                                }
	                            }
	                        }
	                    }
	                }
	            }
	        }
	    }
	    return null;
	}

	/**
	 * Tăng số lượng của dòng sản phẩm đã có
	 */
	private void tangSoLuongDongSanPham(Box row) {
	    Component[] components = row.getComponents();
	    
	    for (Component comp : components) {
	        if (comp instanceof Box) {
	            Box soLuongBox = (Box) comp;
	            // Tìm JTextField số lượng (nằm giữa 2 nút +/-)
	            if (soLuongBox.getComponentCount() == 3) {
	                Component middle = soLuongBox.getComponent(1);
	                if (middle instanceof JTextField) {
	                    JTextField txtSoLuong = (JTextField) middle;
	                    try {
	                        int slHienTai = Integer.parseInt(txtSoLuong.getText().trim());
	                        txtSoLuong.setText(String.valueOf(slHienTai + 1));
	                        
	                        // Trigger sự kiện ActionListener để cập nhật lại giá
	                        ActionListener[] listeners = txtSoLuong.getActionListeners();
	                        if (listeners.length > 0) {
	                            listeners[0].actionPerformed(
	                                new ActionEvent(txtSoLuong, ActionEvent.ACTION_PERFORMED, "")
	                            );
	                        }
	                        return;
	                    } catch (NumberFormatException e) {
	                        // Ignore
	                    }
	                }
	            }
	        }
	    }
	}
}