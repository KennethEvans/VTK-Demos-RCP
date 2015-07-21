/*
 * Program to 
 * Created on Sep 21, 2006
 * By Kenneth Evans, Jr.
 */

package vtkdemos;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import utils.Axes;
import vtk.vtkActor;
import vtk.vtkCamera;
import vtk.vtkPanel;
import vtk.vtkParametricBoy;
import vtk.vtkParametricConicSpiral;
import vtk.vtkParametricDini;
import vtk.vtkParametricEnneper;
import vtk.vtkParametricFigure8Klein;
import vtk.vtkParametricFunctionSource;
import vtk.vtkParametricKlein;
import vtk.vtkParametricMobius;
import vtk.vtkParametricTorus;
import vtk.vtkPolyDataMapper;
import vtk.vtkRenderer;
import demolauncher.VTKDemo;

public class ParametricDemo extends VTKDemo
{
    private static final int DEBUG_LEVEL = 1;

    private vtkRenderer renderer = null;
    vtkParametricFunctionSource source = null;
    vtkActor actor = null;
    vtkParametricMobius moibus = null;
    vtkParametricBoy boy = null;
    vtkParametricConicSpiral spiral = null;
    vtkParametricDini dini = null;
    vtkParametricKlein klein = null;
    vtkParametricFigure8Klein klein8 = null;
    vtkParametricEnneper enneper = null;
    CustomVtkParametricFunction custom = null;

    private static final boolean DO_AXES = true;
    private boolean doAxes = DO_AXES;
    private Axes axes = null;

    public ParametricDemo() {
        super();
        setName("Parametric Equation");
    }

    public String getInfo() {
        String info = "This demo illustrates parametric equations.\n";
        return info;
    }

    private static class CustomVtkParametricFunction extends vtkParametricTorus
    {
        // private static final long serialVersionUID = 1L;

        public CustomVtkParametricFunction() {
            super();
            this.SetRingRadius(3);
            this.SetCrossSectionRadius(2);
            // this.SetMinimumU(0.0);
            // this.SetMinimumV(0.0);
            // this.SetMaximumU(2 * Math.PI);
            // this.SetMaximumV(2 * Math.PI);
            //
            // this.SetJoinU(1);
            // this.SetJoinV(1);
            // this.SetTwistU(0);
            // this.SetTwistV(0);
            // this.SetClockwiseOrdering(1);
            // this.SetDerivativesAvailable(1);
        }

        // public int GetDimension() {
        // return 2;
        // }

        public void Evaluate(double[] uvw, double[] Pt, double[] Duvw) {
            double u = uvw[0];
            double v = uvw[1];
            double[] Du = Duvw;

            double scale = 100;
            double radius = scale * GetRingRadius();
            double crossSectionRadius = scale * GetCrossSectionRadius();

            double cu = Math.cos(u);
            double su = Math.sin(u);
            double cv = Math.cos(v);
            double sv = Math.sin(v);
            double t = radius + crossSectionRadius * cv;

            // The point
            Pt[0] = t * cu;
            Pt[1] = t * su;
            Pt[2] = crossSectionRadius * sv;

            // The derivatives are:
            Du[0] = -t * su;
            Du[1] = t * cu;
            Du[2] = 0;
            Du[3] = -crossSectionRadius * sv * cu;
            Du[4] = -crossSectionRadius * sv * su;
            Du[5] = crossSectionRadius * cv;
        }

        // public double EvaluateScalar(double[] dummy1, double[] dummy2,
        // double[] dummy3) {
        // return 0;
        // }

        // public String Print() {
        // String LF = Utils.LS;
        // String info = super.Print();
        // info += "RingRadius: " + RingRadius + LF;
        // info += "CrossSectionRadius: " + CrossSectionRadius + LF;
        // info += "MinimumU: " + GetMinimumU() + LF;
        // info += "MinimumV: " + GetMinimumV() + LF;
        // info += "MaximumU: " + GetMaximumU() + LF;
        // info += "MaximumV: " + GetMaximumV() + LF;
        // info += "JoinU: " + GetJoinU() + LF;
        // info += "JoinV: " + GetJoinV() + LF;
        // info += "TwistU: " + GetTwistU() + LF;
        // info += "TwistV: " + GetTwistV() + LF;
        // info += "ClockwiseOrdering: " + GetClockwiseOrdering() + LF;
        // info += "DerivativesAvailable: " + GetDerivativesAvailable() + LF;
        // return info;
        // }

        // private double RingRadius = 0;
        // private double CrossSectionRadius = 0;
    }

    public void createPanel() {
        if(created) return;

        // Make a JPanel
        JPanel panel = new JPanel();
        BorderLayout layout = new BorderLayout();
        panel.setLayout(layout);
        setPanel(panel);

        // Make a control panel and add it to the JPanel
        JPanel controlPanel = createControlPanel();
        panel.add(controlPanel, BorderLayout.SOUTH);

        // Make a vtkPanel and add it to the JPanel
        vtkPanel renWin = new vtkPanel();
        panel.add(renWin, BorderLayout.CENTER);

        // Get the renderer
        renderer = renWin.GetRenderer();
        renderer.SetBackground(1, 1, 1); // background color white

        // Define the functions
        custom = new CustomVtkParametricFunction();
        if(DEBUG_LEVEL > 0) {
            System.out.println("Custom:");
            System.out.println(custom);
        }

        moibus = new vtkParametricMobius();
        boy = new vtkParametricBoy();
        spiral = new vtkParametricConicSpiral();
        dini = new vtkParametricDini();
        klein = new vtkParametricKlein();
        klein8 = new vtkParametricFigure8Klein();
        enneper = new vtkParametricEnneper();

        // Define the source
        source = new vtkParametricFunctionSource();
        source.SetParametricFunction(moibus);
        // source.SetScalarModeToDistance();
        source.SetScalarModeToV();

        // Create the mapper
        vtkPolyDataMapper mapper = new vtkPolyDataMapper();
        mapper.SetInput(source.GetOutput());
        mapper.SetScalarRange(-1, 1);

        actor = new vtkActor();
        actor.SetMapper(mapper);
        actor.RotateX(45);
        actor.RotateZ(-10);
        actor.SetScale(1.5);
        renderer.AddActor(actor);

        // Set the camera
        vtkCamera camera = new vtkCamera();
        if(false) {
            camera.SetClippingRange(1, 1000);
            camera.SetFocalPoint(0, 0, 0);
            camera.SetPosition(0, 0, 5);
            camera.SetViewUp(0, 1, 0);
            camera.Zoom(.8);
        }
        renderer.SetActiveCamera(camera);
        renderer.ResetCamera();

        // Add axes
        axes = new Axes(3);
        axes.createActors();
        axes.addActor(renderer);
        axes.setCamera(camera);
        axes.SetVisibility(doAxes ? 1 : 0);
        renderer.ResetCamera();

        created = true;
    }

    public JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();
        FlowLayout layout = new FlowLayout();
        controlPanel.setLayout(layout);

        if(false) {
            JLabel label = new JLabel("Control Panel");
            controlPanel.add(label);
        }

        // Reset
        final JButton resetButton = new JButton("Reset");
        controlPanel.add(resetButton);
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                vtkCamera camera = renderer.GetActiveCamera();
                if(camera != null) {
                    camera.SetViewUp(0, 1, 0);
                    camera.SetPosition(0, 0, 1);
                    camera.SetFocalPoint(0, 0, 0);
                    camera.SetViewAngle(30);
                    camera.SetClippingRange(.1, 1000);
                    camera.ComputeViewPlaneNormal();
                }
                renderer.ResetCamera();
                // renderer.UpdateLightsGeometryToFollowCamera();
                renderer.GetRenderWindow().Render();
                if(DEBUG_LEVEL > 1) {
                    System.out.println("Renderer:");
                    System.out.println(renderer);
                    System.out.println("Camera:");
                    System.out.println(camera);
                }
            }
        });

        // Moibus
        final JButton moibusButton = new JButton("Moibus");
        controlPanel.add(moibusButton);
        moibusButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                source.SetParametricFunction(moibus);
                actor.SetScale(1.5);
                renderer.GetRenderWindow().Render();
            }
        });

        // Spiral
        final JButton spiralButton = new JButton("Spiral");
        controlPanel.add(spiralButton);
        spiralButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                source.SetParametricFunction(spiral);
                actor.SetScale(6);
                renderer.GetRenderWindow().Render();
            }
        });

        // Boy
        final JButton boyButton = new JButton("Boy");
        controlPanel.add(boyButton);
        boyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                source.SetParametricFunction(boy);
                actor.SetScale(2.5);
                renderer.GetRenderWindow().Render();
            }
        });

        // Dini
        final JButton diniButton = new JButton("Dini");
        controlPanel.add(diniButton);
        diniButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                source.SetParametricFunction(dini);
                actor.SetScale(2);
                renderer.GetRenderWindow().Render();
            }
        });

        // Klein
        final JButton kleinButton = new JButton("Klein");
        controlPanel.add(kleinButton);
        kleinButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                source.SetParametricFunction(klein);
                actor.SetScale(1.25);
                renderer.GetRenderWindow().Render();
            }
        });

        // Klein8
        final JButton klein8Button = new JButton("Klein8");
        controlPanel.add(klein8Button);
        klein8Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                source.SetParametricFunction(klein8);
                actor.SetScale(1.5);
                renderer.GetRenderWindow().Render();
            }
        });

        // Enneper
        final JButton enneperButton = new JButton("Enneper");
        controlPanel.add(enneperButton);
        enneperButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                source.SetParametricFunction(enneper);
                actor.SetScale(.5);
                renderer.GetRenderWindow().Render();
            }
        });

        // Custom
        final JButton customButton = new JButton("Custom");
        controlPanel.add(customButton);
        customButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                source.SetParametricFunction(custom);
                actor.SetScale(.5);
                renderer.GetRenderWindow().Render();
            }
        });

        // Axes
        final JCheckBox axesCheck = new JCheckBox();
        axesCheck.setSelected(doAxes);
        axesCheck.setText("Axes");
        controlPanel.add(axesCheck);
        axesCheck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                doAxes = axesCheck.isSelected();
                if(axes != null) {
                    axes.SetVisibility(doAxes ? 1 : 0);
                    renderer.GetRenderWindow().Render();
                }
            }
        });

        return controlPanel;
    }

}
